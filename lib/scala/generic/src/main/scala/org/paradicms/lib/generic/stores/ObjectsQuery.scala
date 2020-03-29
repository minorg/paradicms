package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

final case class ObjectsQuery(
                               collectionUri: Option[Uri] = None,
                               institutionUri: Option[Uri] = None,
                               text: Option[String] = None
                             )
