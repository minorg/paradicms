package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.query.{ParameterizedSparqlString, QueryFactory}
import org.apache.jena.rdf.model.{Property, RDFNode, ResourceFactory}
import org.apache.jena.vocabulary.{DCTerms, DC_11, RDF}
import org.paradicms.lib.generic.models
import org.paradicms.lib.generic.models.domain.vocabulary.CMS
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object}
import org.paradicms.lib.generic.stores._

import scala.collection.JavaConverters._

trait SparqlObjectStore extends ObjectStore with SparqlAccessChecks {
  private def collectionObjectsGraphPatterns(collectionUri: Uri, currentUserUri: Option[Uri], additionalGraphPatterns: List[String] = List()): String =
    accessCheckGraphPatterns(collectionVariable = Some("<" + collectionUri.toString() + ">"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      s"<${collectionUri.toString}> rdf:type cms:Collection .",
      "?institution cms:collection ?collection .",
      "?institution rdf:type cms:Institution .",
      "?collection cms:object ?object .",
      "?object rdf:type cms:Object ."
    ) ++ additionalGraphPatterns)

  override final def getObjectsCount(currentUserUri: Option[Uri], query: ObjectsQuery): Int = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT (COUNT(DISTINCT ?object) AS ?count) WHERE {
         |${objectsQueryGraphPatterns(currentUserUri = currentUserUri, query = query)}
         |}""".stripMargin)
    for ((key, value) <- objectsQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  override final def getObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, query: ObjectsQuery): GetObjectsResult = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT ?collection ?institution ?object WHERE {
         |${objectsQueryGraphPatterns(currentUserUri = currentUserUri, query = query)}
         |}
         |LIMIT ${limit}
         |OFFSET ${offset}
         |""".stripMargin)
    for ((key, value) <- objectsQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      val querySolutions = queryExecution.execSelect().asScala.toList.map(querySolution => (
        Uri.parse(querySolution.get("collection").asResource().getURI),
        Uri.parse(querySolution.get("institution").asResource().getURI),
        Uri.parse(querySolution.get("object").asResource().getURI)
      ))
      val collectionUris = querySolutions.map(querySolution => querySolution._1)
      val institutionUris = querySolutions.map(querySolution => querySolution._2)
      val objectUris = querySolutions.map(querySolution => querySolution._3)

      val collections = getCollectionsByUris(collectionUris = collectionUris.toSet.toList, currentUserUri = currentUserUri)
      val institutions = getInstitutionsByUris(institutionUris = institutionUris.toSet.toList, currentUserUri = currentUserUri) //.map(institution => institution.uri -> institution).toMap
      val objectsByUri: Map[Uri, models.domain.Object] = getObjectsByUris(objectUris = objectUris, currentUserUri = currentUserUri).map(object_ => object_.uri -> object_).toMap

      GetObjectsResult(
        collections = collections,
        facets = getObjectFacets(currentUserUri = currentUserUri, query = query),
        institutions = institutions,
        objectsWithContext = querySolutions.map(querySolution => ObjectWithContext(
          collectionUri = querySolution._1,
          institutionUri = querySolution._2,
          object_ = objectsByUri(querySolution._3)
        ))
      )
    }
  }

  private def getObjectFacets(currentUserUri: Option[Uri], query: ObjectsQuery): ObjectFacets =
    ObjectFacets(
      subjects = getObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.subject, DC_11.subject), query = query)
        .filter(node => node.isLiteral)
        .map(node => node.asLiteral().getString)
        .toSet
    )

  private def getObjectFacet(currentUserUri: Option[Uri], properties: List[Property], query: ObjectsQuery): List[RDFNode] = {
    val propertyWherePattern =
    //      if (properties.size == 1)
      s"?object <${properties(0).getURI}> ?facet ."
    //      else
    //        "{ " + properties.map(property => s"{ ?object <${property.getURI}> ?facet . }").mkString(" UNION ") + " }"

    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT DISTINCT ?facet WHERE {
         |${objectsQueryGraphPatterns(currentUserUri = currentUserUri, query = query, additionalGraphPatterns = List(propertyWherePattern))}
         |}""".stripMargin)
    for ((key, value) <- objectsQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      val resultSet = queryExecution.execSelect()
      return resultSet.asScala.map(querySolution => querySolution.get("facet")).toList
    }
  }

  private def objectsQueryGraphPatterns(currentUserUri: Option[Uri], query: ObjectsQuery, additionalGraphPatterns: List[String] = List()): String =
    accessCheckGraphPatterns(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
      "?institution rdf:type cms:Institution .",
      "?institution cms:collection ?collection .",
      "?collection rdf:type cms:Collection .",
      "?collection cms:object ?object .",
      "?object rdf:type cms:Object .",
    ) ++ query.text.map(_ => List("?object text:query ?text .")).getOrElse(List())
      ++ additionalGraphPatterns)

  private def objectsQueryParams(currentUserUri: Option[Uri], query: ObjectsQuery): Map[String, RDFNode] = {
    (
      query.collectionUri.flatMap(collectionUri => Some(("collection", ResourceFactory.createResource(collectionUri.toString())))) ++
        query.text.flatMap(text => Some(("text", ResourceFactory.createStringLiteral(text)))) ++
        List()
      ).toMap
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
         |${PREFIXES}
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
