package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.query.QueryFactory
import org.apache.jena.vocabulary.RDF
import org.paradicms.lib.base.stores.sparql.{SparqlAccessCheckGraphPatterns, SparqlConnectionLoanPatterns}
import org.paradicms.lib.generic.models.domain.Institution
import org.paradicms.lib.generic.rdf.vocabularies.CMS
import org.paradicms.lib.generic.stores.InstitutionStore

import scala.collection.JavaConverters._

trait SparqlInstitutionStore extends InstitutionStore with SparqlAccessCheckGraphPatterns with SparqlConnectionLoanPatterns with GenericSparqlPrefixes {
  override final def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = {
    // Should be safe to inject institutionUri since it's already been parsed as a URI
    val institutionVariable = "<" + institutionUri.toString() + ">"
    val queryWhere =
      accessCheck(collectionVariable = None, currentUserUri = currentUserUri, institutionVariable = institutionVariable, objectVariable = None, queryPatterns = List(
        s"$institutionVariable rdf:type cms:Institution .",
        s"$institutionVariable ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |${GENERIC_SPARQL_PREFIXES}
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

  override final def getInstitutions(currentUserUri: Option[Uri]): List[Institution] = {
    val queryWhere =
      accessCheck(collectionVariable = None, currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = None, queryPatterns = List(
        "?institution rdf:type cms:Institution .",
        "?institution ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |${GENERIC_SPARQL_PREFIXES}
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

  override final def getInstitutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution] = {
    if (institutionUris.isEmpty) {
      return List()
    }

    // Should be safe to inject institutionUris since they've already been parsed as URIs
    val queryWhere =
      accessCheck(collectionVariable = None, currentUserUri = currentUserUri, institutionVariable = "?institution", objectVariable = None, queryPatterns = List(
        s"VALUES ?institution { ${institutionUris.map(institutionUri => "<" + institutionUri.toString() + ">").mkString(" ")} }",
        "?institution rdf:type cms:Institution .",
        "?institution ?p ?o ."
      ))

    val query = QueryFactory.create(
      s"""
         |${GENERIC_SPARQL_PREFIXES}
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
}
