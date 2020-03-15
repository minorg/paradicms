package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.query.{ParameterizedSparqlString, QueryFactory}
import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.RDF
import org.paradicms.lib.generic.models
import org.paradicms.lib.generic.models.domain.vocabulary.CMS
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object, ObjectSearchResult}
import org.paradicms.lib.generic.stores.ObjectStore

import scala.collection.JavaConverters._

trait SparqlObjectStore extends ObjectStore with SparqlAccessChecks {
  override final def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[models.domain.Object] = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = getCollectionObjectUris(collectionUri = collectionUri, currentUserUri = currentUserUri, limit = limit, offset = offset))
  }

  protected final def getCollectionObjectUris(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Uri] = {
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

  override final def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = {
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

  private def collectionObjectsGraphPatterns(collectionUri: Uri, currentUserUri: Option[Uri]): String =
    accessCheckGraphPatterns(collectionVariable = Some("<" + collectionUri.toString() + ">"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      s"<${collectionUri.toString}> rdf:type cms:Collection .",
      "?institution cms:collection ?collection .",
      "?institution rdf:type cms:Institution .",
      "?collection cms:object ?object .",
      "?object rdf:type cms:Object ."
    ))

  override final def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String): Int = {
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

  private def matchingObjectsGraphPatterns(currentUserUri: Option[Uri]): String =
    accessCheckGraphPatterns(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      "?institution rdf:type cms:Institution .",
      "?institution cms:collection ?collection .",
      "?collection rdf:type cms:Collection .",
      "?collection cms:object ?object .",
      "?object rdf:type cms:Object .",
      "?object text:query ?text ."
    ))

  override final def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult] = {
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
      val objectsByUri: Map[Uri, models.domain.Object] = getObjectsByUris(objectUris = objectUris, currentUserUri = currentUserUri).map(object_ => object_.uri -> object_).toMap

      querySolutions.map(querySolution => ObjectSearchResult(
        collection = collectionsByUri(querySolution._1),
        institution = institutionsByUri(querySolution._2),
        object_ = objectsByUri(querySolution._3)
      ))
    }
  }

  override final def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): models.domain.Object = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = List(objectUri)).head
  }

  protected final def getObjectsByUris(currentUserUri: Option[Uri], objectUris: List[Uri]): List[models.domain.Object] = {
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

  def getCollectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection]

  def getInstitutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution]
}
