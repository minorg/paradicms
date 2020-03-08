package org.paradicms.lib.generic.models

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{DomainModel, DomainModelCompanion}

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