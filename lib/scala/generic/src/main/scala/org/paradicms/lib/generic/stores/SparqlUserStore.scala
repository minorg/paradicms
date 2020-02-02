package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.apache.jena.query.{ParameterizedSparqlString, QueryFactory}
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.RDF
import org.paradicms.lib.generic.models.domain.User
import org.paradicms.lib.generic.models.domain.vocabulary.CMS

import scala.collection.JavaConverters._

trait SparqlUserStore extends UserStore with SparqlConnectionLoanPatterns {
  override final def getUserByUri(userUri: Uri): Option[User] =
    getUsersByUris(List(userUri)).headOption

  protected final def getUsersByUris(userUris: List[Uri]): List[User] = {
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

  override final def putUser(user: User) = {
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
}
