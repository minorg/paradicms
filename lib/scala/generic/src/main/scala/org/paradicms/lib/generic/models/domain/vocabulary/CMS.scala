package org.paradicms.lib.generic.models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object CMS {
  val URI = "http://www.paradicms.org/ns#"

  // Properties
  val owner = ResourceFactory.createProperty(URI + "owner")

  // Classes
  val Collection = ResourceFactory.createResource(URI + "Collection")
  val Image = ResourceFactory.createResource(URI + "Image")
  val Institution = ResourceFactory.createResource(URI + "Institution")
  val Object = ResourceFactory.createResource(URI + "Object")
  val User = ResourceFactory.createResource(URI + "User")

  // Instances
  val inherit = ResourceFactory.createResource(URI + "inherit")
  val public = ResourceFactory.createResource(URI + "public")
}
