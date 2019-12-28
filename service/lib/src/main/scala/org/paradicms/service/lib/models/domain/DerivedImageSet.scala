package models.domain

import org.apache.jena.sparql.vocabulary.FOAF

final case class DerivedImageSet(original: Image, thumbnail: Option[Image])

object DerivedImageSet extends DomainModelCompanion {
  def apply(originalResource: ResourceWrapper): DerivedImageSet =
    DerivedImageSet(
      original = Image(originalResource.resource),
      thumbnail = originalResource.getPropertyObject(FOAF.thumbnail).flatMap(object_ => if (object_.isResource()) Some(Image(object_.asResource())) else None)
    )
}
