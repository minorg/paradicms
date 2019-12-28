package models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object CMS {
  val URI = "http://www.dressdiscover.org/ns/cms#"

  // Resources
  val Collection = ResourceFactory.createResource(URI + "Collection")
  val Image = ResourceFactory.createResource(URI + "Image")
  val Institution = ResourceFactory.createResource(URI + "Institution")
  val Object = ResourceFactory.createResource(URI + "Object")
}
