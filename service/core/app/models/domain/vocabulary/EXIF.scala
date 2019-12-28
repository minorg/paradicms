package models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object EXIF {
  val URI = "http://www.w3.org/2003/12/exif/ns#"

  // Properties
  val height = ResourceFactory.createProperty(URI + "height")
  val width = ResourceFactory.createProperty(URI + "width")
}
