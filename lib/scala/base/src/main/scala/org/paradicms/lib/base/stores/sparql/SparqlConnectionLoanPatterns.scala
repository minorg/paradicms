package org.paradicms.lib.base.stores.sparql

import org.apache.jena.query.{Query, QueryExecution}
import org.apache.jena.rdfconnection.RDFConnection

trait SparqlConnectionLoanPatterns {
  protected def withQueryExecution[T](query: Query)(f: (QueryExecution) => T): T

  protected def withRdfConnection[T]()(f: (RDFConnection) => T): T
}
