package org.paradicms.service.lib.generic.stores

import io.lemonlabs.uri.{Uri, Url}
import org.apache.jena.query._
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.RDF
import org.paradicms.service.lib.generic.models.domain
import org.paradicms.service.lib.generic.models.domain._
import org.paradicms.service.lib.generic.models.domain.vocabulary.CMS
import play.api.Configuration

import scala.collection.JavaConverters._

class SparqlStore(sparqlQueryUrl: Url, sparqlUpdateUrl: Url) extends Store {
  def this(configuration: Configuration) = this(
    sparqlQueryUrl = Url.parse(configuration.getOptional[String]("sparqlQueryUrl").getOrElse("http://fuseki:3030/ds/sparql")),
    sparqlUpdateUrl = Url.parse(configuration.getOptional[String]("sparqlUpdateUrl").getOrElse("http://fuseki:3030/ds/update"))
  )

  /**
   * Create ready-to-use WHERE block contents that wrap common query-specific patterns in access check UNION blocks
   */
  private def accessCheckGraphPatterns(collectionVariable: Option[String], currentUserUri: Option[Uri], institutionVariable: String, objectVariable: Option[String], queryPatterns: List[String]): String = {
    var unionPatterns: List[List[String]] = institutionAccessCheckGraphPatterns(currentUserUri = currentUserUri, institutionVariable = institutionVariable, unionPatterns = List(queryPatterns))
    if (collectionVariable.isDefined) {
      unionPatterns = collectionAccessCheckGraphPatterns(collectionVariable = collectionVariable.get, currentUserUri = currentUserUri, unionPatterns = unionPatterns)
    }
    if (objectVariable.isDefined) {
      unionPatterns = objectAccessCheckGraphPatterns(currentUserUri = currentUserUri, objectVariable = objectVariable.get, unionPatterns = unionPatterns)
    }
    unionPatterns.map(unionPattern => s"{ ${unionPattern.mkString("\n")} }").mkString("\nUNION\n")
  }

  /**
   * See institutionAccessChecks description.
   */
  private def collectionAccessCheckGraphPatterns(collectionVariable: String, currentUserUri: Option[Uri], unionPatterns: List[List[String]]): List[List[String]] =
    inheritableAccessCheckGraphPatterns(currentUserUri = currentUserUri, modelVariable = collectionVariable, unionPatterns = unionPatterns)

  private def collectionObjectsGraphPatterns(collectionUri: Uri, currentUserUri: Option[Uri]): String =
    accessCheckGraphPatterns(collectionVariable = Some("<" + collectionUri.toString() + ">"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      s"<${collectionUri.toString}> rdf:type cms:Collection .",
      "?institution cms:collection ?collection .",
      "?institution rdf:type cms:Institution .",
      "?collection cms:object ?object .",
      "?object rdf:type cms:Object ."
    ))

  /**
   * Given a list of union patterns (a list of lists of graph patterns), add permutations for an institution access check and return a new list of union patterns
   */
  private def institutionAccessCheckGraphPatterns(currentUserUri: Option[Uri], institutionVariable: String, unionPatterns: List[List[String]]): List[List[String]] = {
    val public = s"$institutionVariable cms:owner cms:public ."
    if (currentUserUri.isDefined) {
      val private_ = s"$institutionVariable cms:owner <${currentUserUri.get}> ."
      List(public, private_).flatMap(accessCheckPattern => unionPatterns.map(unionPattern => accessCheckPattern +: unionPattern))
    } else {
      unionPatterns.map(unionPattern => public +: unionPattern)
    }
  }

  private def inheritableAccessCheckGraphPatterns(currentUserUri: Option[Uri], modelVariable: String, unionPatterns: List[List[String]]): List[List[String]] = {
    val public = s"$modelVariable cms:owner cms:inherit ."
    if (currentUserUri.isDefined) {
      val private_ = s"$modelVariable cms:owner <${currentUserUri.get}> ."
      List(public, private_).flatMap(accessCheckPattern => unionPatterns.map(unionPattern => accessCheckPattern +: unionPattern))
    } else {
      unionPatterns.map(unionPattern => public +: unionPattern)
    }
  }

  private def matchingObjectsGraphPatterns(currentUserUri: Option[Uri]): String =
    accessCheckGraphPatterns(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      "?institution rdf:type cms:Institution .",
      "?institution cms:collection ?collection .",
      "?collection rdf:type cms:Collection .",
      "?collection cms:object ?object .",
      "?object rdf:type cms:Object .",
      "?object text:query ?text ."
    ))

  private def objectAccessCheckGraphPatterns(currentUserUri: Option[Uri], objectVariable: String, unionPatterns: List[List[String]]): List[List[String]] =
    inheritableAccessCheckGraphPatterns(currentUserUri = currentUserUri, modelVariable = objectVariable, unionPatterns = unionPatterns)

  override def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = {
    getCollectionsByUris(collectionUris = List(collectionUri), currentUserUri = currentUserUri).head
  }

  private def getCollectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection] = {
    // Should be safe to inject collectionUris since they've already been parsed as URIs
    val queryWhere =
      accessCheckGraphPatterns(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = None, queryPatterns = List(
        s"VALUES ?collection { ${
          collectionUris.map(collectionUri => "<" + collectionUri.toString() + ">").mkString(" ")
        } }",
        "?collection rdf:type cms:Collection .",
        "?institution cms:collection ?collection .",
        "?institution rdf:type cms:Institution .",
        "?collection ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?collection ?p ?o .
         |} WHERE {
         |$queryWhere
         |}
         |""".stripMargin)
    withQueryExecution(query) {
      queryExecution =>
        val model = queryExecution.execConstruct()
        model.listSubjectsWithProperty(RDF.`type`, CMS.Collection).asScala.toList.map(resource => Collection(resource))
    }
  }

  override def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[domain.Object] = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = getCollectionObjectUris(collectionUri = collectionUri, currentUserUri = currentUserUri, limit = limit, offset = offset))
  }

  override def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = {
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT (COUNT(DISTINCT ?object) AS ?count)
         |WHERE {
         |${collectionObjectsGraphPatterns(collectionUri, currentUserUri)}
         |}
         |""".stripMargin)
    withQueryExecution(query) {
      queryExecution =>
        queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  private def getCollectionObjectUris(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Uri] = {
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT DISTINCT ?object WHERE {
         |${collectionObjectsGraphPatterns(collectionUri, currentUserUri)}
         |} LIMIT $limit OFFSET $offset
         |""".stripMargin)
    withQueryExecution(query) {
      queryExecution =>
        queryExecution.execSelect().asScala.toList.map(querySolution => Uri.parse(querySolution.get("object").asResource().getURI))
    }
  }

  override def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = {
    // Should be safe to inject institutionUri since it's already been parsed as a URI
    val institutionVariable = "<" + institutionUri.toString() + ">"
    val queryWhere =
      accessCheckGraphPatterns(collectionVariable = None, currentUserUri = currentUserUri, institutionVariable = institutionVariable, objectVariable = None, queryPatterns = List(
        s"$institutionVariable rdf:type cms:Institution .",
        s"$institutionVariable ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  $institutionVariable ?p ?o
         |} WHERE {
         |$queryWhere
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      val institutions = model.listSubjectsWithProperty(RDF.`type`, CMS.Institution).asScala.toList.map(resource => Institution(resource))
      if (!institutions.isEmpty) institutions(0) else throw new NoSuchElementException
    }
  }

  override def getInstitutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection] = {
    // Should be safe to inject institutionUri since it's already been parsed as a URI
    val institutionVariable = "<" + institutionUri.toString() + ">"
    val queryWhere =
      accessCheckGraphPatterns(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = institutionVariable, objectVariable = None, queryPatterns = List(
        s"$institutionVariable rdf:type cms:Institution .",
        s"$institutionVariable cms:collection ?collection .",
        "?collection rdf:type cms:Collection .",
        "?collection ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?collection ?p ?o
         |} WHERE {
         |$queryWhere
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Collection).asScala.toList.map(resource => Collection(resource))
    }
  }

  override def getInstitutions(currentUserUri: Option[Uri]): List[Institution] = {
    val queryWhere =
      accessCheckGraphPatterns(collectionVariable = None, currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = None, queryPatterns = List(
        "?institution rdf:type cms:Institution .",
        "?institution ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?institution ?p ?o
         |} WHERE {
         |$queryWhere
         |}
         |""".stripMargin
    )

    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Institution).asScala.toList.map(resource => Institution(resource))
    }
  }

  private def getInstitutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution] = {
    // Should be safe to inject institutionUris since they've already been parsed as URIs
    val queryWhere =
      accessCheckGraphPatterns(collectionVariable = None, currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = None, queryPatterns = List(
        s"VALUES ?institution { ${institutionUris.map(institutionUri => "<" + institutionUri.toString() + ">").mkString(" ")} }",
        "?institution rdf:type cms:Institution .",
        "?institution ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?institution ?p ?o .
         |} WHERE {
         |$queryWhere
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Institution).asScala.toList.map(resource => Institution(resource))
    }
  }

  override def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult] = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |PREFIX text: <http://jena.apache.org/text#>
         |SELECT ?collection ?institution ?object WHERE {
         |${matchingObjectsGraphPatterns(currentUserUri)}
         |}
         |LIMIT ${limit}
         |OFFSET ${offset}
         |""".stripMargin)
    queryString.setParam("text", ResourceFactory.createPlainLiteral(text))
    val query = queryString.asQuery()
    withQueryExecution(query) { queryExecution =>
      val querySolutions = queryExecution.execSelect().asScala.toList.map(querySolution => (
        Uri.parse(querySolution.get("collection").asResource().getURI),
        Uri.parse(querySolution.get("institution").asResource().getURI),
        Uri.parse(querySolution.get("object").asResource().getURI)
      ))
      val collectionUris = querySolutions.map(querySolution => querySolution._1)
      val institutionUris = querySolutions.map(querySolution => querySolution._2)
      val objectUris = querySolutions.map(querySolution => querySolution._3)

      val collectionsByUri = getCollectionsByUris(collectionUris = collectionUris.toSet.toList, currentUserUri = currentUserUri).map(collection => collection.uri -> collection).toMap
      val institutionsByUri = getInstitutionsByUris(institutionUris = institutionUris.toSet.toList, currentUserUri = currentUserUri).map(institution => institution.uri -> institution).toMap
      val objectsByUri: Map[Uri, domain.Object] = getObjectsByUris(objectUris = objectUris, currentUserUri = currentUserUri).map(object_ => object_.uri -> object_).toMap

      querySolutions.map(querySolution => ObjectSearchResult(
        collection = collectionsByUri(querySolution._1),
        institution = institutionsByUri(querySolution._2),
        object_ = objectsByUri(querySolution._3)
      ))
    }
  }

  override def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String): Int = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |PREFIX text: <http://jena.apache.org/text#>
         |SELECT (COUNT(DISTINCT ?object) AS ?count) WHERE {
         |${matchingObjectsGraphPatterns(currentUserUri)}
         |}""".stripMargin)
    queryString.setParam("text", ResourceFactory.createPlainLiteral(text))
    val query = queryString.asQuery()
    withQueryExecution(query) { queryExecution =>
      queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  override def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): domain.Object = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = List(objectUri)).head
  }

  private def getObjectsByUris(currentUserUri: Option[Uri], objectUris: List[Uri]): List[domain.Object] = {
    // Should be safe to inject objectUris since they've already been parsed as URIs
    val queryWhere = accessCheckGraphPatterns(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      "?institution rdf:type cms:Institution .",
      "?institution cms:collection ?collection .",
      "?collection rdf:type cms:Collection .",
      "?collection cms:object ?object .",
      s"VALUES ?object { ${objectUris.map(objectUri => "<" + objectUri.toString() + ">").mkString(" ")} }",
      "?object rdf:type cms:Object .",
      "?object ?objectP ?objectO .",
      "OPTIONAL { ?object foaf:depiction ?originalImage . ?originalImage rdf:type cms:Image . ?originalImage ?originalImageP ?originalImageO . OPTIONAL { ?originalImage foaf:thumbnail ?thumbnailImage . ?thumbnailImage rdf:type cms:Image . ?thumbnailImage ?thumbnailImageP ?thumbnailImageO . } }"
    ))

    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX foaf: <${FOAF.getURI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?object ?objectP ?objectO .
         |  ?object foaf:depiction ?originalImage .
         |  ?originalImage ?originalImageP ?originalImageO .
         |  ?originalImage foaf:thumbnail ?thumbnailImage .
         |  ?thumbnailImage ?thumbnailImageP ?thumbnailImageO .
         |} WHERE {
         |$queryWhere
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      //      model.listSubjectsWithProperty(RDF.`type`, CMS.Object).asScala.toList.foreach(resource => model.listStatements(resource, null, null).asScala.foreach(System.out.println(_)))
      model.listSubjectsWithProperty(RDF.`type`, CMS.Object).asScala.toList.map(resource => Object(resource))
    }
  }

  override def getUserByUri(userUri: Uri): Option[User] =
    getUsersByUris(List(userUri)).headOption

  private def getUsersByUris(userUris: List[Uri]): List[User] = {
    // Should be safe to inject userUris since they've already been parsed as URIs
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?user ?p ?o .
         |} WHERE {
         |  VALUES ?user { ${userUris.map(userUri => "<" + userUri.toString() + ">").mkString(" ")} }
         |  ?user rdf:type cms:User .
         |  ?user ?p ?o .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.User).asScala.toList.map(resource => User(resource))
    }
  }

  override def putUser(user: User) = {
    val emailStatement = if (user.email.isDefined) s"<${user.uri.toString()}> foaf:mbox <mailto:${user.email.get}> ." else ""
    val update =
      new ParameterizedSparqlString(
        s"""
           |PREFIX cms: <${CMS.URI}>
           |PREFIX foaf: <${FOAF.getURI}>
           |PREFIX rdf: <${RDF.getURI}>
           |INSERT DATA {
           |  GRAPH <urn:system:user> {
           |    <${user.uri.toString()}> rdf:type cms:User .
           |    ${emailStatement}
           |    <${user.uri.toString()}> foaf:name ?name .
           |  }
           |}
           |""".stripMargin)
    update.setLiteral("name", user.name)
    withRdfConnection() { rdfConnection =>
      rdfConnection.update(update.asUpdate())
    }
  }

  private def withQueryExecution[T](query: Query)(f: (QueryExecution) => T): T = {
    val queryExecution = QueryExecutionFactory.sparqlService(sparqlQueryUrl.toString(), query)
    try {
      f(queryExecution)
    } finally {
      queryExecution.close()
    }
  }

  private def withRdfConnection[T]()(f: (RDFConnection) => T): T = {
    val rdfConnection = RDFConnectionFactory.connectFuseki(sparqlUpdateUrl.toString())
    try {
      f(rdfConnection)
    } finally {
      rdfConnection.close()
    }
  }
}
