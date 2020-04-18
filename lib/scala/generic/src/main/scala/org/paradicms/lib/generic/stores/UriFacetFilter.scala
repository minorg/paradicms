package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

final case class UriFacetFilter(exclude: Option[List[Uri]], include: Option[List[Uri]])
