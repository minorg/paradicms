package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class Institution(
                              name: String,
                              rights: Option[Rights] = None,
                              uri: Uri
                            )
