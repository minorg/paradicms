package models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object SCHEMA {
  val URI = "http://schema.org/"

  val pageCount = ResourceFactory.createProperty(URI + "numberOfPages")
}
