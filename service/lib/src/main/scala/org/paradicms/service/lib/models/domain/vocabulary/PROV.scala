package models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object PROV {
  val URI = "http://www.w3.org/ns/prov#"

  // Properties
  val wasDerivedFrom = ResourceFactory.createProperty(URI + "wasDerivedFrom")
}
