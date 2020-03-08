package org.paradicms.lib.generic.models.domain.vocabulary

import org.apache.jena.rdf.model.ResourceFactory

object CONTACT {
  val URI = "http://www.w3.org/2000/10/swap/pim/contact#"

  // Properties
  val sortName = ResourceFactory.createProperty(URI + "sortName")
}
