package org.paradicms.lib.generic.stores.sparql

import org.apache.jena.query.text.{EntityDefinition, TextDatasetFactory, TextIndexConfig}
import org.apache.jena.query.{DatasetFactory, Query, QueryExecution, QueryExecutionFactory}
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.apache.jena.vocabulary.{DCTerms, DC_11, RDFS}
import org.apache.lucene.store.RAMDirectory
import org.paradicms.lib.base.stores.sparql.SparqlConnectionLoanPatterns


abstract class DatasetSparqlStore extends SparqlConnectionLoanPatterns {
  private var memDataset = DatasetFactory.create()
  // jena-text example adapted from JenaTextExample1.java
  private val entityDefinition = new EntityDefinition("uri", "text");
  entityDefinition.setPrimaryPredicate(RDFS.label.asNode())
  entityDefinition.set("text", DCTerms.title.asNode())
  entityDefinition.set("text", DC_11.title.asNode())
  // Add other mappings from assembler.ttl as needed
  private val directory = new RAMDirectory()
  protected val dataset = TextDatasetFactory.createLucene(memDataset, directory, new TextIndexConfig(entityDefinition))

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
