package org.paradicms.lib.generic.stores.sparql

import io.lemonlabs.uri.Uri
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.paradicms.lib.base.stores.sparql.SparqlConnectionLoanPatterns

/**
 * Store that reads a .ttl in test/resources into a Dataset and queries it with SPARQL.
 *
 * This is a class instead of an object so that it can be "reset" by creating a new instance.
 */
abstract class TestSparqlStore extends DatasetSparqlStore with SparqlConnectionLoanPatterns {
  RDFDataMgr.read(dataset, getClass.getResourceAsStream("/test_data.ttl"), Lang.TURTLE)

  val currentUserUri = Some(Uri.parse("http://example.com/user"))
}
