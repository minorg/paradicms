package org.paradicms.service.lib.stores

import io.lemonlabs.uri.{Uri, Url}
import org.apache.jena.query._
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.RDF
import org.paradicms.service.lib.models.domain.vocabulary.CMS
import org.paradicms.service.lib.models.domain.{Collection, Institution, Object, ObjectSearchResult, User}

import scala.collection.JavaConverters._

class SparqlStore(endpointUrl: Url) extends Store {
  private val institutionsQuery = QueryFactory.create(
    s"""
       |PREFIX cms: <${CMS.URI}>
       |PREFIX rdf: <${RDF.getURI}>
       |CONSTRUCT WHERE {
       |  ?institution rdf:type cms:Institution .
       |  ?institution ?p ?o .
       |}
       |""".stripMargin)

  override def collectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = {
    collectionsByUris(collectionUris = List(collectionUri), currentUserUri = currentUserUri).head
  }

  private def collectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection] = {
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
         |  ?collection ?p ?o .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Collection).asScala.toList.map(resource => Collection(resource))
    }
  }

  override def collectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object] = {
    objectsByUris(currentUserUri = currentUserUri, objectUris = collectionObjectUris(collectionUri = collectionUri, currentUserUri = currentUserUri, limit = limit, offset = offset))
  }

  override def collectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = {
    // Should be safe to inject collectionUri since it's already been parsed as a URI
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT (COUNT(DISTINCT ?object) AS ?count)
         |WHERE {
         |  <${collectionUri.toString()}> cms:object ?object .
         |  ?object rdf:type cms:Object .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  private def collectionObjectUris(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Uri] = {
    // Should be safe to inject collectionUri since it's already been parsed as a URI
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |SELECT DISTINCT ?object WHERE {
         |  <${collectionUri.toString()}> cms:object ?object .
         |  ?object rdf:type cms:Object .
         |} LIMIT ${limit} OFFSET ${offset}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      queryExecution.execSelect().asScala.toList.map(querySolution => Uri.parse(querySolution.get("object").asResource().getURI))
    }
  }

  override def institutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = {
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

  override def institutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection] = {
    // Should be safe to inject institutionUri since it's already been parsed as a URI
    val query = QueryFactory.create(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |CONSTRUCT WHERE {
         |  <${institutionUri.toString()}> cms:collection ?collection .
         |  ?collection rdf:type cms:Collection .
         |  ?collection ?p ?o .
         |}
         |""".stripMargin)
    withQueryExecution(query) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Collection).asScala.toList.map(resource => Collection(resource))
    }
  }

  override def institutions(currentUserUri: Option[Uri]): List[Institution] = {
    withQueryExecution(institutionsQuery) { queryExecution =>
      val model = queryExecution.execConstruct()
      model.listSubjectsWithProperty(RDF.`type`, CMS.Institution).asScala.toList.map(resource => Institution(resource))
    }
  }

  private def institutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution] = {
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

  override def matchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult] = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |PREFIX text: <http://jena.apache.org/text#>
         |SELECT ?collection ?institution ?object WHERE {
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

      val collectionsByUri = collectionsByUris(collectionUris = collectionUris.toSet.toList, currentUserUri = currentUserUri).map(collection => collection.uri -> collection).toMap
      val institutionsByUri = institutionsByUris(institutionUris = institutionUris.toSet.toList, currentUserUri = currentUserUri).map(institution => institution.uri -> institution).toMap
      val objectsByUri: Map[Uri, Object] = objectsByUris(objectUris = objectUris, currentUserUri = currentUserUri).map(object_ => object_.uri -> object_).toMap

      querySolutions.map(querySolution => ObjectSearchResult(
        collection = collectionsByUri(querySolution._1),
        institution = institutionsByUri(querySolution._2),
        object_ = objectsByUri(querySolution._3)
      ))
    }
  }

  override def matchingObjectsCount(currentUserUri: Option[Uri], text: String): Int = {
    // Should be safe to inject collectionUri since it's already been parsed as a URI
    val queryString = new ParameterizedSparqlString(
      s"""
         |PREFIX cms: <${CMS.URI}>
         |PREFIX rdf: <${RDF.getURI}>
         |PREFIX text: <http://jena.apache.org/text#>
         |SELECT (COUNT(DISTINCT ?object) AS ?count) WHERE {
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

  override def objectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object = {
    objectsByUris(currentUserUri = currentUserUri, objectUris = List(objectUri)).head
  }

  private def objectsByUris(currentUserUri: Option[Uri], objectUris: List[Uri]): List[Object] = {
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

  private def withQueryExecution[T](query: Query)(f: (QueryExecution) => T): T = {
    val queryExecution = QueryExecutionFactory.sparqlService(endpointUrl.toString(), query)
    try {
      f(queryExecution)
    } finally {
      queryExecution.close()
    }
  }

  override def userByUri(userUri: Uri): Option[User] =
    usersByUris(List(userUri)).headOption

  private def usersByUris(userUris: List[Uri]): List[User] = {
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
}
