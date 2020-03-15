package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class Person(
                         familyName: Option[String],
                         givenName: Option[String],
                         name: Option[String],
                         sortName: Option[String],
                         uri: Uri
                       ) extends DomainModel

object Person extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): Person =
    Person(
      familyName=resource.foaf.familyName(),
      givenName=resource.foaf.givenName(),
      name=resource.foaf.name(),
      sortName=resource.contact.sortName(),
      uri=resource.uri
    )
}