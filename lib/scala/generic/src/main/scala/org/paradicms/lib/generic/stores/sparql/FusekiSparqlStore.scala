package org.paradicms.lib.generic.stores.sparql

import org.apache.jena.query.{Query, QueryExecution, QueryExecutionFactory}
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.paradicms.lib.base.stores.sparql.SparqlConnectionLoanPatterns

trait FusekiSparqlStore extends SparqlConnectionLoanPatterns {
  protected val configuration: FusekiSparqlStoreConfiguration

  protected final def withQueryExecution[T](query: Query)(f: (QueryExecution) => T): T = {
    val queryExecution = QueryExecutionFactory.sparqlService(configuration.sparqlQueryUrl.toString(), query)
    try {
      f(queryExecution)
    } finally {
      queryExecution.close()
    }
  }

  protected final def withRdfConnection[T]()(f: (RDFConnection) => T): T = {
    val rdfConnection = RDFConnectionFactory.connectFuseki(configuration.sparqlUpdateUrl.toString())
    try {
      f(rdfConnection)
    } finally {
      rdfConnection.close()
    }
  }
}
