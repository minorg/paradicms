package org.paradicms.lib.generic.stores.sparql

import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.RDF
import org.paradicms.lib.generic.rdf.vocabularies.CMS

trait GenericSparqlPrefixes {
  protected val GENERIC_SPARQL_PREFIXES =
    s"""
      |PREFIX cms: <${CMS.URI}>
      |PREFIX foaf: <${FOAF.getURI}>
      |PREFIX rdf: <${RDF.getURI}>
      |PREFIX text: <http://jena.apache.org/text#>
      |""".stripMargin
}
