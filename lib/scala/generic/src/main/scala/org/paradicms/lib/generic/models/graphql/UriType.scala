package org.paradicms.lib.generic.models.graphql

import io.lemonlabs.uri.Uri
import sangria.schema.{ScalarAlias, StringType}

object UriType extends ScalarAlias[Uri, String](
  StringType, _.toString, uri => Right(Uri.parse(uri))
)
