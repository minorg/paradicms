package org.paradicms.service.lib.models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object CMS {
  val URI = "http://www.paradicms.org/ns#"

  // Properties
  val collectionOwner = ResourceFactory.createProperty(URI + "owner")

  // Resources
  val Collection = ResourceFactory.createResource(URI + "Collection")
  val Image = ResourceFactory.createResource(URI + "Image")
  val Institution = ResourceFactory.createResource(URI + "Institution")
  val Object = ResourceFactory.createResource(URI + "Object")
  val public = ResourceFactory.createResource(URI + "public")
  val User = ResourceFactory.createResource(URI + "User")
}
