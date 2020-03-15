package org.paradicms.lib.generic.models.domain

import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.models.domain.FoafResourceProperties

final case class DerivedImageSet(original: Image, thumbnail: Option[Image])

object DerivedImageSet {
  implicit class DerivedImageSetResource(val resource: Resource) extends FoafResourceProperties

  def apply(resource: DerivedImageSetResource): DerivedImageSet =
    DerivedImageSet(
      original = Image(resource.resource),
      thumbnail = resource.thumbnails.headOption.map(resource => Image(resource))
    )
}
