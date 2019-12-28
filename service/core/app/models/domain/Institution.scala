package models.domain

import io.lemonlabs.uri.Uri

final case class Institution(
                              name: String,
                              rights: Option[Rights] = None,
                              uri: Uri
                            ) extends DomainModel {
}

object Institution extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): Institution =
    Institution(
      name = resource.foaf.name.get,
      rights = Rights(resource.resource),
      uri = resource.uri
    )
}
