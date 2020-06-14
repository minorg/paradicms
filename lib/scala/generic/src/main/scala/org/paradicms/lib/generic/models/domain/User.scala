package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class User(email: Option[String], name: String, uri: Uri)
