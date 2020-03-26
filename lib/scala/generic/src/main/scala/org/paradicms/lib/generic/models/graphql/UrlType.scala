package org.paradicms.lib.generic.models.graphql

import io.lemonlabs.uri.{Uri, Url}
import sangria.schema.{ScalarAlias, StringType}
import sangria.validation.StringCoercionViolation

object UrlType extends ScalarAlias[Url, String](
  StringType, _.toString, uriString => {
    val uri = Uri.parse(uriString)
    if (uri.isInstanceOf[Url]) Right(uri.asInstanceOf[Url]) else Left(StringCoercionViolation)
  }
)
