package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.query.{Query, QueryExecution}
import org.apache.jena.rdfconnection.RDFConnection

abstract class TestSparqlStore extends SparqlConnectionLoanPatterns {
  val currentUserUri = Some(Uri.parse("http://example.com/user"))

  protected final def withQueryExecution[T](query: Query)(f: (QueryExecution) => T): T = {
    throw new UnsupportedOperationException
//    val queryExecution = QueryExecutionFactory.sparqlService(configuration.sparqlQueryUrl.toString(), query)
//    try {
//      f(queryExecution)
//    } finally {
//      queryExecution.close()
//    }
  }

  protected final def withRdfConnection[T]()(f: (RDFConnection) => T): T = {
    throw new UnsupportedOperationException
//    val rdfConnection = RDFConnectionFactory.connectFuseki(configuration.sparqlUpdateUrl.toString())
//    try {
//      f(rdfConnection)
//    } finally {
//      rdfConnection.close()
//    }
  }
}
