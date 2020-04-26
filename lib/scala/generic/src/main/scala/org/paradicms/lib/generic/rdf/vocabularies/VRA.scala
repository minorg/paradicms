package org.paradicms.lib.generic.rdf.vocabularies

import org.apache.jena.rdf.model.ResourceFactory

object VRA {
  val URI = "http://purl.org/vra/"

  // Properties
  val culturalContext = ResourceFactory.createProperty(URI + "culturalContext")
  val hasTechnique = ResourceFactory.createProperty(URI + "hasTechnique")
  val material = ResourceFactory.createProperty(URI + "material")
}
