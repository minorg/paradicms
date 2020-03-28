package org.paradicms.lib.generic.models.graphql

import org.paradicms.lib.generic.models.domain.{DerivedImageSet, Image, Rights, User}
import sangria.macros.derive.{ExcludeFields, ReplaceField, deriveObjectType}
import sangria.schema.{Argument, Field, IntType, OptionType, StringType, fields}

abstract class AbstractGraphQlSchemaDefinition {
  implicit val uriType = UriType

  // Scalar argument types
  val LimitArgument = Argument("limit", IntType, description = "Limit")
  val OffsetArgument = Argument("offset", IntType, description = "Offset")
  val TextArgument = Argument("text", StringType, description = "Text")
  val UriArgument = Argument("uri", UriType, description = "URI")

  // Intentionally limit the fields exposed on User
  implicit val CurrentUserType = sangria.schema.ObjectType("CurrentUser", fields[Unit, User](
    Field("name", StringType, resolve = _.value.name)
  ))

  // Image
  implicit val ImageType = deriveObjectType[Unit, Image](
    ReplaceField("url", Field("url", UrlType, resolve = _.value.url))
  )

  implicit val DerivedImageSetType = deriveObjectType[Unit, DerivedImageSet](
  )

  // Rights
  implicit val RightsType = deriveObjectType[Unit, Rights](
  )
}
