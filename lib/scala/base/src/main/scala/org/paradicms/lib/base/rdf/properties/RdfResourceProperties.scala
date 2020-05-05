package org.paradicms.lib.base.rdf.properties

import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.RDF

trait RdfResourceProperties extends ResourceProperties {
  final def types = getPropertyObjectResources(RDF.`type`)
  final def types_=(typeResources: List[Resource]): Unit = setProperty(RDF.`type`, typeResources)
}
