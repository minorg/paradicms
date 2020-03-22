package org.paradicms.lib.generic.models.graphql

import io.lemonlabs.uri.Url
import sangria.schema.{ScalarAlias, StringType}

object UrlType extends ScalarAlias[Url, String](
  StringType, _.toString, uri => Right(Url.parse(uri))
)
