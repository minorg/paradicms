package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class Person(
                         familyName: Option[String],
                         givenName: Option[String],
                         name: Option[String],
                         sortName: Option[String],
                         uri: Uri
                       )
