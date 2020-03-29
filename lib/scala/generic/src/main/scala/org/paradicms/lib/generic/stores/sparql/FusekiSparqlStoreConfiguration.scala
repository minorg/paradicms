package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Url
import play.api.Configuration

final case class FusekiSparqlStoreConfiguration(sparqlQueryUrl: Url, sparqlUpdateUrl: Url)

object FusekiSparqlStoreConfiguration {
  def apply(configuration: Configuration): FusekiSparqlStoreConfiguration =
    FusekiSparqlStoreConfiguration(
      sparqlQueryUrl = Url.parse(configuration.getOptional[String]("sparqlQueryUrl").getOrElse("http://fuseki:3030/ds/sparql")),
      sparqlUpdateUrl = Url.parse(configuration.getOptional[String]("sparqlUpdateUrl").getOrElse("http://fuseki:3030/ds/update"))
    )
}