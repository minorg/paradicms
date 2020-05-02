package org.paradicms.lib.generic.models.domain

import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.FoafResourceProperties

final case class DerivedImageSet(original: Image, derived: List[Image] = List())

object DerivedImageSet {
  implicit class DerivedImageSetResource(val resource: Resource) extends FoafResourceProperties

  def apply(resource: DerivedImageSetResource): DerivedImageSet =
    DerivedImageSet(
      original = Image(resource.resource),
      derived = resource.thumbnails.map(resource => Image(resource))
    )
}
