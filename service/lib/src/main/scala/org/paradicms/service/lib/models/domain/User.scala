package org.paradicms.service.lib.models.domain

import io.lemonlabs.uri.Uri

final case class User(name: String, uri: Uri) extends DomainModel

object User extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): User =
    User(
      name = resource.foaf.name.get,
      uri = resource.uri
    )
}

