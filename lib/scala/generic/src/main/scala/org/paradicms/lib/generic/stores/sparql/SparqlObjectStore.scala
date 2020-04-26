package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.query.{ParameterizedSparqlString, QueryFactory}
import org.apache.jena.rdf.model.{Property, RDFNode, ResourceFactory}
import org.apache.jena.vocabulary.{DCTerms, DC_11, RDF}
import org.paradicms.lib.generic.models
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object}
import org.paradicms.lib.generic.rdf.vocabularies.{CMS, VRA}
import org.paradicms.lib.generic.stores._
import org.slf4j.Logger

import scala.collection.JavaConverters._

trait SparqlObjectStore extends ObjectStore with SparqlConnectionLoanPatterns with SparqlPrefixes {
  protected val logger: Logger

  private object GraphPatterns extends SparqlAccessCheckGraphPatterns {
    private def objectFilters(filters: ObjectFilters): List[String] =
      uriFacetFilter(filters.collectionUris, "?collection") ++
      stringFacetFilter(filters.culturalContexts, List(VRA.culturalContext), "?culturalContext") ++
      uriFacetFilter(filters.institutionUris, "?institution") ++
      stringFacetFilter(filters.materials, List(VRA.material), "?material") ++
      stringFacetFilter(filters.spatials, List(DCTerms.spatial), "?spatial") ++
      stringFacetFilter(filters.subjects, List(DCTerms.subject, DC_11.subject), "?subject") ++
      stringFacetFilter(filters.techniques, List(VRA.hasTechnique), "?technique") ++
      stringFacetFilter(filters.temporals, List(DCTerms.temporal), "?temporal") ++
      stringFacetFilter(filters.types, List(DCTerms.`type`, DC_11.`type`), "?type")

    private def objectFiltersParams(filters: ObjectFilters): Map[String, RDFNode] =
      stringFacetFilterParams(filters.culturalContexts, "?culturalContext") ++
      stringFacetFilterParams(filters.materials, "?material") ++
      stringFacetFilterParams(filters.spatials, "?spatial") ++
      stringFacetFilterParams(filters.subjects, "?subject") ++
      stringFacetFilterParams(filters.techniques, "?technique") ++
      stringFacetFilterParams(filters.temporals, "?temporal") ++
      stringFacetFilterParams(filters.types, "?type")

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

    def objectQueryParams(currentUserUri: Option[Uri], query: ObjectQuery): Map[String, RDFNode] =
      query.filters.map(filters => objectFiltersParams(filters)).getOrElse(Map()) ++
      query.text.map(text => Map("text" -> ResourceFactory.createStringLiteral(text))).getOrElse(Map())

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

    private def stringFacetFilter(filter: StringFacetFilter, properties: List[Property], variable: String): List[String] =
      // Declares a new variable
      List(s"?object ${properties.map(property => "<" + property.getURI + ">").mkString(" | ")} ${variable} .") ++
      filter.include.map(includes => List(s"FILTER ( ${variable} IN ( ${includes.indices.map(includeIndex => variable + "_include_" + includeIndex).mkString(", ")} ) )")).getOrElse(List()) ++
      filter.exclude.map(excludes => List(s"FILTER ( ${variable} NOT IN ( ${excludes.indices.map(excludeIndex => variable + "_exclude_" + excludeIndex).mkString(", ")} ) )")).getOrElse(List())

    private def stringFacetFilter(filter: Option[StringFacetFilter], properties: List[Property], variable: String): List[String] =
      filter.map(filter => stringFacetFilter(filter, properties, variable)).getOrElse(List())

    private def stringFacetFilterParams(filter: StringFacetFilter, variable: String): Map[String, RDFNode] =
      filter.include.map(includes =>
        includes.zipWithIndex.map(
          includeWithIndex => ((variable + "_include_" + includeWithIndex._2), ResourceFactory.createStringLiteral(includeWithIndex._1))
      ).toMap).getOrElse(Map()) ++
        filter.exclude.map(excludes =>
          excludes.zipWithIndex.map(
            excludeWithIndex => ((variable + "_exclude_" + excludeWithIndex._2), ResourceFactory.createStringLiteral(excludeWithIndex._1))
          ).toMap).getOrElse(Map())

    private def stringFacetFilterParams(filter: Option[StringFacetFilter], variable: String): Map[String, RDFNode] =
      filter.map(filter => stringFacetFilterParams(filter, variable)).getOrElse(Map())

    private def uriFacetFilter(filter: UriFacetFilter, variable: String): List[String] =
      // Assumes the variable has already been defined
        filter.include.map(includes => List(s"FILTER ( ${variable} IN ( ${includes.map(include => "<" + include + ">").mkString(", ")} ) )")).getOrElse(List()) ++
        filter.exclude.map(excludes => List(s"FILTER ( ${variable} NOT IN ( ${excludes.map(exclude => "<" + exclude + ">").mkString(", ")} ) )")).getOrElse(List())

    private def uriFacetFilter(filter: Option[UriFacetFilter], variable: String): List[String] =
      filter.map(filter => uriFacetFilter(filter, variable)).getOrElse(List())
  }

  override final def getObjectsCount(currentUserUri: Option[Uri], query: ObjectQuery): Int = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT (COUNT(DISTINCT ?object) AS ?count) WHERE {
         |${GraphPatterns.objectQuery(currentUserUri = currentUserUri, query = query)}
         |}""".stripMargin)
    for ((key, value) <- GraphPatterns.objectQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      val count = queryExecution.execSelect().next().get("count").asLiteral().getInt
      logger.debug("getObjectsCount: {} -> {}", queryString, count)
      count
    }
  }

  override final def getObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, query: ObjectQuery, cachedCollectionsByUri: Map[Uri, Collection] = Map(), cachedInstitutionsByUri: Map[Uri, Institution]= Map()): GetObjectsResult = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT DISTINCT ?collection ?institution ?object WHERE {
         |${GraphPatterns.objectQuery(currentUserUri = currentUserUri, query = query)}
         |}
         |LIMIT ${limit}
         |OFFSET ${offset}
         |""".stripMargin)
    for ((key, value) <- GraphPatterns.objectQueryParams(currentUserUri = currentUserUri, query = query)) {
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
        institutions = institutions,
        objectsWithContext = querySolutions.map(querySolution => ObjectWithContext(
          collectionUri = querySolution._1,
          institutionUri = querySolution._2,
          object_ = objectsByUri(querySolution._3)
        ))
      )
    }
  }

  def getObjectFacets(currentUserUri: Option[Uri], query: ObjectQuery, cachedCollectionsByUri: Map[Uri, Collection] = Map(), cachedInstitutionsByUri: Map[Uri, Institution] = Map()): GetObjectFacetsResult = {
    val facets =
      ObjectFacets(
        culturalContexts = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(VRA.culturalContext), query = query),
        materials = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(VRA.material), query = query),
        spatials = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.spatial), query = query),
        subjects = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.subject, DC_11.subject), query = query),
        techniques = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(VRA.hasTechnique), query = query),
        temporals = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.temporal), query = query),
        types = getStringObjectFacet(currentUserUri = currentUserUri, properties = List(DCTerms.`type`, DC_11.`type`), query = query)
      )
    // TODO: collection and institution will eventually be facets
    GetObjectFacetsResult(
      collections = List(),
      facets = facets,
      institutions = List()
    )
  }

  private def getObjectFacet(currentUserUri: Option[Uri], properties: List[Property], query: ObjectQuery): List[RDFNode] = {
    val queryString = new ParameterizedSparqlString(
      s"""
         |${PREFIXES}
         |SELECT DISTINCT ?facet WHERE {
         |${GraphPatterns.objectQuery(currentUserUri = currentUserUri, query = query, additionalGraphPatterns = List(s"?object ${properties.map(property => "<" + property.getURI + ">" ).mkString(" | ")} ?facet ."))}
         |}""".stripMargin)
    for ((key, value) <- GraphPatterns.objectQueryParams(currentUserUri = currentUserUri, query = query)) {
      queryString.setParam(key, value)
    }
    withQueryExecution(queryString.asQuery()) { queryExecution =>
      val resultSet = queryExecution.execSelect()
      return resultSet.asScala.map(querySolution => querySolution.get("facet")).toList
    }
  }

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

  private def getStringObjectFacet(currentUserUri: Option[Uri], properties: List[Property], query: ObjectQuery): List[String] =
    getObjectFacet(currentUserUri = currentUserUri, properties = properties, query = query)
      .filter(node => node.isLiteral)
      .map(node => node.asLiteral().getString)
      .toSet
      .toList
}
