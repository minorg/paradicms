package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class Collection(
                             description: Option[String] = None,
                             name: String,
                             rights: Option[Rights] = None,
                             uri: Uri
                           )
