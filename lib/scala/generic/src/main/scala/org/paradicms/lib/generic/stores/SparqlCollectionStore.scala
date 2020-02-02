package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.apache.jena.query.QueryFactory
import org.apache.jena.vocabulary.RDF
import org.paradicms.lib.generic.models.domain.Collection
import org.paradicms.lib.generic.models.domain.vocabulary.CMS

import scala.collection.JavaConverters._

trait SparqlCollectionStore extends CollectionStore with SparqlAccessChecks {
  override final def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = {
    getCollectionsByUris(collectionUris = List(collectionUri), currentUserUri = currentUserUri).head
  }

  override final def getCollectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection] = {
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

  override final def getInstitutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection] = {
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
}
