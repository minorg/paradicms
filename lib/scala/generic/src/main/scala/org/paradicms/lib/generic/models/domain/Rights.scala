package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class Rights(
                         holder: Option[String] = None,
                         license: Option[Uri] = None,
                         statementUri: Option[Uri] = None,
                         text: Option[String] = None
                       )
