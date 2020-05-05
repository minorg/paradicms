package org.paradicms.lib.base.rdf.vocabularies

import org.apache.jena.rdf.model.ResourceFactory

object PROV {
  val PREFIX = "prov"
  val URI = "http://www.w3.org/ns/prov#"

  // Properties
  val actedOnBehalfOf = ResourceFactory.createProperty(URI + "actedOnBehalfOf")
  val endedAtTime = ResourceFactory.createProperty(URI + "endedAtTime")
  val generated = ResourceFactory.createProperty(URI + "generated")
  val hadDerivation = ResourceFactory.createProperty(URI + "hadDerivation")
  //  val hadGeneration = ResourceFactory.createProperty(URI + "hadGeneration")
  val startedAtTime = ResourceFactory.createProperty(URI + "startedAtTime")
  val used = ResourceFactory.createProperty(URI + "used")
  val wasAssociatedWith = ResourceFactory.createProperty(URI + "wasAssociatedWith")
  val wasAttributedTo = ResourceFactory.createProperty(URI + "wasAttributedTo")
  val wasDerivedFrom = ResourceFactory.createProperty(URI + "wasDerivedFrom")
  val wasGeneratedBy = ResourceFactory.createProperty(URI + "wasGeneratedBy")
  val wasInformedBy = ResourceFactory.createProperty(URI + "wasInformedBy")

  // Resources
  val Activity = ResourceFactory.createResource(URI + "Activity")
  val Agent = ResourceFactory.createResource(URI + "Agent")
  val Entity = ResourceFactory.createResource(URI + "Entity")
  //  val Derivation = ResourceFactory.createResource(URI + "Derivation")
  //  val Generation = ResourceFactory.createResource(URI + "Generation")
  val Organization = ResourceFactory.createResource(URI + "Organization")
  val Person = ResourceFactory.createResource(URI + "Person")
}
