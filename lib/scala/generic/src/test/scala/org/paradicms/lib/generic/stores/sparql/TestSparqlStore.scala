package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.query.{DatasetFactory, Query, QueryExecution, QueryExecutionFactory}
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}

/**
 * Store that reads a .ttl in test/resources into a Dataset and queries it with SPARQL.
 *
 * This is a class instead of an object so that it can be "reset" by creating a new instance.
 */
abstract class TestSparqlStore extends SparqlConnectionLoanPatterns {
  private val dataset = DatasetFactory.create()
  RDFDataMgr.read(dataset, getClass.getResourceAsStream("/test_data.ttl"), Lang.TURTLE)

  val currentUserUri = Some(Uri.parse("http://example.com/user"))

  protected final def withQueryExecution[T](query: Query)(f: (QueryExecution) => T): T = {
    val queryExecution = QueryExecutionFactory.create(query, dataset)
    try {
      f(queryExecution)
    } finally {
      queryExecution.close()
    }
  }

  protected final def withRdfConnection[T]()(f: (RDFConnection) => T): T = {
    val rdfConnection = RDFConnectionFactory.connect(dataset)
    try {
      f(rdfConnection)
    } finally {
      rdfConnection.close()
    }
  }
}
