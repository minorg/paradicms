package org.paradicms.service.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class User(email: Option[String], name: String, uri: Uri) extends DomainModel

object User extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): User =
    User(
      email = resource.foaf.mbox.map(uri => uri.path.toString()),
      name = resource.foaf.name.get,
      uri = resource.uri
    )
}

