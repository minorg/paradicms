package org.paradicms.service.lib.stores

import io.lemonlabs.uri.{Uri, Url}
import org.apache.jena.query._
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.RDF
import org.paradicms.service.lib.models.domain.vocabulary.CMS
import org.paradicms.service.lib.models.domain.{Collection, Institution, Object, ObjectSearchResult, User}
import play.api.Configuration

import scala.collection.JavaConverters._

class SparqlStore(sparqlQueryUrl: Url, sparqlUpdateUrl: Url) extends Store {
  def this(configuration: Configuration) = this(
    sparqlQueryUrl = Url.parse(configuration.get[String]("sparqlQueryUrl")),
    sparqlUpdateUrl = Url.parse(configuration.get[String]("sparqlUpdateUrl"))
  )

  private val institutionsQuery = QueryFactory.create(
    s"""
       |PREFIX cms: <${CMS.URI}>
       |PREFIX rdf: <${RDF.getURI}>
       |CONSTRUCT WHERE {
       |  ?institution rdf:type cms:Institution .
       |  ?institution ?p ?o .
       |}
       |""".stripMargin)

  private def collectionAccessCheck(collectionVariable: String, currentUserUri: Option[Uri]): String =
    if (currentUserUri.isDefined)
      s"{ ${collectionVariable} cms:collectionOwner cms:public } UNION { ${collectionVariable} cms:collectionOwner <${currentUserUri.get.toString()}> }"
    else
      s"${collectionVariable} cms:collectionOwner cms:public"

  override def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = {
    getCollectionsByUris(collectionUris = List(collectionUri), currentUserUri = currentUserUri).head
  }

  private def getCollectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection] = {
    // Should be safe to inject collectionUris since they've already been parsed as URIs
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?collection ?p ?o .
         |} WHERE {
         |  VALUES ?collection { ${collectionUris.map(collectionUri => "<" + collectionUri.toString() + ">").mkString(" ")} }
         |  ?collection rdf:type cms:Collection .
         |  ${collectionAccessCheck("?collection", currentUserUri)}
         |  ?collection ?p ?o .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Collection).asScala.toList.map(resource => Collection(resource))
    }
  }

  override def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object] = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = getCollectionObjectUris(collectionUri = collectionUri, currentUserUri = currentUserUri, limit = limit, offset = offset))
  }

  override def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = {
    // Should be safe to inject collectionUri since it's already been parsed as a URI
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT (COUNT(DISTINCT ?object) AS ?count)
         |WHERE {
         |  <${collectionUri.toString()}> rdf:type cms:Collection .
         |  ${collectionAccessCheck("<" + collectionUri.toString() + ">", currentUserUri)}
         |  <${collectionUri.toString()}> cms:object ?object .
         |  ?object rdf:type cms:Object .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  private def getCollectionObjectUris(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Uri] = {
    // Should be safe to inject collectionUri since it's already been parsed as a URI
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT DISTINCT ?object WHERE {
         |  <${collectionUri.toString()}> rdf:type cms:Collection .
         |  ${collectionAccessCheck("<" + collectionUri.toString() + ">", currentUserUri)}
         |  <${collectionUri.toString()}> cms:object ?object .
         |  ?object rdf:type cms:Object .
         |} LIMIT ${limit} OFFSET ${offset}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      queryExecution.execSelect().asScala.toList.map(querySolution => Uri.parse(querySolution.get("object").asResource().getURI))
    }
  }

  override def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = {
    // Should be safe to inject institutionUri since it's already been parsed as a URI
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT WHERE {
         |  <${institutionUri.toString()}> rdf:type cms:Institution .
         |  <${institutionUri.toString()}> ?p ?o .
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
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT WHERE {
         |  <${institutionUri.toString()}> cms:collection ?collection .
         |  ?collection rdf:type cms:Collection .
         |  ${collectionAccessCheck("?collection", currentUserUri)}
         |  ?collection ?p ?o .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Collection).asScala.toList.map(resource => Collection(resource))
    }
  }

  override def getInstitutions(currentUserUri: Option[Uri]): List[Institution] = {
    withQueryExecution(institutionsQuery) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Institution).asScala.toList.map(resource => Institution(resource))
    }
  }

  private def getInstitutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution] = {
    // Should be safe to inject institutionUris since they've already been parsed as URIs
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT {
         |  ?institution ?p ?o .
         |} WHERE {
         |  VALUES ?institution { ${institutionUris.map(institutionUri => "<" + institutionUri.toString() + ">").mkString(" ")} }
         |  ?institution rdf:type cms:Institution .
         |  ?institution ?p ?o .
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
         |  ?collection rdf:type cms:Collection .
         |  ${collectionAccessCheck("?collection", currentUserUri)}
         |  ?collection cms:object ?object .
         |  ?institution cms:collection ?collection .
         |  ?object rdf:type cms:Object .
         |  ?object text:query ?text
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
      val objectsByUri: Map[Uri, Object] = getObjectsByUris(objectUris = objectUris, currentUserUri = currentUserUri).map(object_ => object_.uri -> object_).toMap

      querySolutions.map(querySolution => ObjectSearchResult(
        collection = collectionsByUri(querySolution._1),
        institution = institutionsByUri(querySolution._2),
        object_ = objectsByUri(querySolution._3)
      ))
    }
  }

  override def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String): Int = {
    // Should be safe to inject collectionUri since it's already been parsed as a URI
    val queryString = new ParameterizedSparqlString(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |PREFIX text: <http://jena.apache.org/text#>
         |SELECT (COUNT(DISTINCT ?object) AS ?count) WHERE {
         |  ?collection rdf:type cms:Collection .
         |  ${collectionAccessCheck("?collection", currentUserUri)}
         |  ?collection cms:object ?object .
         |  ?object rdf:type cms:Object .
         |  ?object text:query ?text
         |}
         |""".stripMargin)
    queryString.setParam("text", ResourceFactory.createPlainLiteral(text))
    val query = queryString.asQuery()
    withQueryExecution(query) { queryExecution =>
      queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  override def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = List(objectUri)).head
  }

  private def getObjectsByUris(currentUserUri: Option[Uri], objectUris: List[Uri]): List[Object] = {
    // Should be safe to inject objectUris since they've already been parsed as URIs
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
         |  VALUES ?object { ${objectUris.map(objectUri => "<" + objectUri.toString() + ">").mkString(" ")} }
         |  ?object rdf:type cms:Object .
         |  ?collection rdf:type cms:Collection .
         |  ${collectionAccessCheck("?collection", currentUserUri)}
         |  ?collection cms:object ?object .
         |  ?object ?objectP ?objectO .
         |  OPTIONAL {
         |    ?object foaf:depiction ?originalImage .
         |    ?originalImage rdf:type cms:Image .
         |    ?originalImage ?originalImageP ?originalImageO .
         |    OPTIONAL {
         |      ?originalImage foaf:thumbnail ?thumbnailImage .
         |      ?thumbnailImage rdf:type cms:Image .
         |      ?thumbnailImage ?thumbnailImageP ?thumbnailImageO .
         |    }
         |  }
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
