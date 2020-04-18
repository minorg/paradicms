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

trait SparqlObjectStore extends ObjectStore with SparqlConnectionLoanPatterns with SparqlPrefixes {
  private object GraphPatterns extends SparqlAccessCheckGraphPatterns {
//    def collectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], additionalGraphPatterns: List[String] = List()): String =
//      accessCheck(collectionVariable = Some("<" + collectionUri.toString() + ">"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
//        s"<${collectionUri.toString}> rdf:type cms:Collection .",
//        "?institution cms:collection ?collection .",
//        "?institution rdf:type cms:Institution .",
//        "?collection cms:object ?object .",
//        "?object rdf:type cms:Object ."
//      ) ++ additionalGraphPatterns)

    private def objectFilters(filters: ObjectFilters): List[String] =
//      query.collectionUri.map(collectionUri => List(s"VALUES ?collection { <${collectionUri}> }")).getOrElse(List())
      List()

    def objectQuery(currentUserUri: Option[Uri], query: ObjectQuery, additionalGraphPatterns: List[String] = List()): String =
      accessCheck(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
        "?institution rdf:type cms:Institution .",
        "?institution cms:collection ?collection .",
        "?collection rdf:type cms:Collection .",
        "?collection cms:object ?object .",
        "?object rdf:type cms:Object .",
      ) ++ query.filters.map(filters => objectFilters(filters)).getOrElse(List())
        ++ query.text.map(_ => List("?object text:query ?text .")).getOrElse(List())
        ++ additionalGraphPatterns)

    def objectsByUris(currentUserUri: Option[Uri], objectUris: List[Uri]): String =
      accessCheck(collectionVariable = Some("?collection"), currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = Some("?object"), queryPatterns = List(
        "?institution rdf:type cms:Institution .",
        "?institution cms:collection ?collection .",
        "?collection rdf:type cms:Collection .",
        "?collection cms:object ?object .",
        s"VALUES ?object { ${objectUris.map(objectUri => "<" + objectUri.toString() + ">").mkString(" ")} }",
        "?object rdf:type cms:Object .",
        "?object ?objectP ?objectO .",
        "OPTIONAL { ?object foaf:depiction ?originalImage . ?originalImage rdf:type cms:Image . ?originalImage ?originalImageP ?originalImageO . OPTIONAL { ?originalImage foaf:thumbnail ?thumbnailImage . ?thumbnailImage rdf:type cms:Image . ?thumbnailImage ?thumbnailImageP ?thumbnailImageO . } }"
      ))
  }

  override final def getObjectsCount(currentUserUri: Option[Uri], query: ObjectQuery): Int = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT (COUNT(DISTINCT ?object) AS ?count) WHERE {
         |${GraphPatterns.objectQuery(currentUserUri = currentUserUri, query = query)}
         |}""".stripMargin)
    for ((key, value) <- objectQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      queryExecution.execSelect().next().get("count").asLiteral().getInt
    }
  }

  override final def getObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, query: ObjectQuery, cachedCollectionsByUri: Map[Uri, Collection] = Map()): GetObjectsResult = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT ?collection ?institution ?object WHERE {
         |${GraphPatterns.objectQuery(currentUserUri = currentUserUri, query = query)}
         |}
         |LIMIT ${limit}
         |OFFSET ${offset}
         |""".stripMargin)
    for ((key, value) <- objectQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      val querySolutions = queryExecution.execSelect().asScala.toList.map(querySolution => (
        Uri.parse(querySolution.get("collection").asResource().getURI),
        Uri.parse(querySolution.get("institution").asResource().getURI),
        Uri.parse(querySolution.get("object").asResource().getURI)
      ))
      val collectionUris = querySolutions.map(querySolution => querySolution._1).toSet
      val institutionUris = querySolutions.map(querySolution => querySolution._2).toSet
      val objectUris = querySolutions.map(querySolution => querySolution._3).toSet

      val (cachedCollectionUris, missingCollectionUris) = collectionUris.partition(collectionUri => cachedCollectionsByUri.contains(collectionUri))
      val collections =
        cachedCollectionUris.toList.map(collectionUri => cachedCollectionsByUri(collectionUri)) ++
        getCollectionsByUris(collectionUris = missingCollectionUris.toList, currentUserUri = currentUserUri)
      val institutions = getInstitutionsByUris(institutionUris = institutionUris.toList, currentUserUri = currentUserUri) //.map(institution => institution.uri -> institution).toMap
      val objectsByUri: Map[Uri, models.domain.Object] = getObjectsByUris(objectUris = objectUris.toList, currentUserUri = currentUserUri).map(object_ => object_.uri -> object_).toMap

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

  private def getObjectFacets(currentUserUri: Option[Uri], query: ObjectQuery): ObjectFacets =
    ObjectFacets(
      subjects = getObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.subject, DC_11.subject), query = query)
        .filter(node => node.isLiteral)
        .map(node => node.asLiteral().getString)
        .toSet,
      types = getObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.`type`, DC_11.`type`), query = query)
        .filter(node => node.isLiteral)
        .map(node => node.asLiteral().getString)
        .toSet
    )

  private def getObjectFacet(currentUserUri: Option[Uri], properties: List[Property], query: ObjectQuery): List[RDFNode] = {
    val propertyWherePattern =
    //      if (properties.size == 1)
      s"?object <${properties(0).getURI}> ?facet ."
    //      else
    //        "{ " + properties.map(property => s"{ ?object <${property.getURI}> ?facet . }").mkString(" UNION ") + " }"

    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT DISTINCT ?facet WHERE {
         |${GraphPatterns.objectQuery(currentUserUri = currentUserUri, query = query, additionalGraphPatterns = List(propertyWherePattern))}
         |}""".stripMargin)
    for ((key, value) <- objectQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      val resultSet = queryExecution.execSelect()
      return resultSet.asScala.map(querySolution => querySolution.get("facet")).toList
    }
  }

  private def objectQueryParams(currentUserUri: Option[Uri], query: ObjectQuery): Map[String, RDFNode] =
      query.text.flatMap(text => Some(("text", ResourceFactory.createStringLiteral(text)))).toMap

  override final def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): models.domain.Object = {
    getObjectsByUris(currentUserUri = currentUserUri, objectUris = List(objectUri)).head
  }

  protected final def getObjectsByUris(currentUserUri: Option[Uri], objectUris: List[Uri]): List[models.domain.Object] = {
    if (objectUris.isEmpty) {
      return List()
    }
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
         |${GraphPatterns.objectsByUris(currentUserUri = currentUserUri, objectUris = objectUris)}
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
