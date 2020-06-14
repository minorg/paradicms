package org.paradicms.lib.generic.models.graphql

import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, DecodingFailure, HCursor}
import io.lemonlabs.uri.{Uri, Url}
import org.paradicms.lib.generic.models.domain._
import sangria.macros.derive._
import sangria.marshalling.circe._
import sangria.schema.{Argument, Field, IntType, OptionType, ScalarAlias, StringType, fields}
import sangria.validation.{BaseViolation, Violation}

abstract class AbstractGraphQlSchemaDefinition {
  // Scalar aliases
  implicit object UriType extends ScalarAlias[Uri, String](
    StringType, _.toString, uri => {
      val result = Url.parseTry(uri)
      if (result.isSuccess) {
        Right(result.get)
      } else {
        Left(new BaseViolation(result.failed.get.getMessage) {}.asInstanceOf[Violation])
      }
    }
  )

  object UrlType extends ScalarAlias[Url, String](
    StringType, _.toString, uri => {
      val result = Url.parseTry(uri)
      if (result.isSuccess) {
        Right(result.get)
      } else {
        Left(new BaseViolation(result.failed.get.getMessage) {}.asInstanceOf[Violation])
      }
    }
  )

  // Scalar decoders
  implicit val uriDecoder: Decoder[Uri] = (c: HCursor) => {
    val result = Uri.parseTry(c.value.asString.get)
    if (result.isSuccess) {
      Right(result.get)
    } else {
      Left(DecodingFailure.fromThrowable(result.failed.get, c.history))
    }
  }

  implicit val urlDecoder: Decoder[Url] = (c: HCursor) => {
    val result = Url.parseTry(c.value.asString.get)
    if (result.isSuccess) {
      Right(result.get)
    } else {
      Left(DecodingFailure.fromThrowable(result.failed.get, c.history))
    }
  }

  // Scalar argument types
  val LimitArgument = Argument("limit", IntType, description = "Limit")
  val OffsetArgument = Argument("offset", IntType, description = "Offset")
  val UriArgument = Argument("uri", UriType, description = "URI")

  // Complex decoders
  implicit val derivedImageSetDecoder: Decoder[DerivedImageSet] = deriveDecoder
  implicit val imageDecoder: Decoder[Image] = deriveDecoder
  implicit val imageDimensionsDecoder: Decoder[ImageDimensions] = deriveDecoder

  // Complex input types
  implicit val ImageDimensionsInputType = deriveInputObjectType[ImageDimensions](
    InputObjectTypeName("ImageDimensionsInput")
  )

  // Complex argument types
  val MaxDimensionsArgument = Argument("maxDimensions", ImageDimensionsInputType)

  // Object types
  // Intentionally limit the fields exposed on User
  implicit val CurrentUserType = sangria.schema.ObjectType("CurrentUser", fields[Unit, User](
    Field("name", StringType, resolve = _.value.name)
  ))

  implicit val ImageDimensionsType = deriveObjectType[Unit, ImageDimensions]()
  implicit val ImageType = deriveObjectType[Unit, Image](
    ReplaceField("url", Field("url", UrlType, resolve = _.value.url))
  )

  def selectThumbnail(derivedImageSet: DerivedImageSet, maxDimensions: ImageDimensions): Option[Image] = {
    if (derivedImageSet.derived.isEmpty) {
      return None
    }

    val candidateImagesWithDimensions =
      derivedImageSet.derived.flatMap(image =>
        if (image.exactDimensions.isDefined) {
          Some((image, image.exactDimensions.get))
        } else if (image.maxDimensions.isDefined) {
          Some((image, image.maxDimensions.get))
        } else {
          None
        }).filter(imageWithDimensions => maxDimensions.contains(imageWithDimensions._2))

    if (candidateImagesWithDimensions.isEmpty) {
      return None
    }

    implicit val imageDimensionsOrderingSmallestToLargest: Ordering[ImageDimensions] = (x, y) =>
        if (x.contains(y)) {
          1 // x > y
        } else if (y.contains(x)) {
          -1 // x < y
        } else {
          0 // equivalent for the purposes of ordering
        }

    Some(candidateImagesWithDimensions.maxBy(imageWithDimensions => imageWithDimensions._2)._1)
  }

  implicit val DerivedImageSetType = deriveObjectType[Unit, DerivedImageSet](
    AddFields(Field("thumbnail", OptionType(ImageType), arguments = MaxDimensionsArgument :: Nil, resolve = ctx => selectThumbnail(derivedImageSet = ctx.value, maxDimensions = ctx.args.arg(MaxDimensionsArgument))))
  )

  implicit val RightsType = deriveObjectType[Unit, Rights]()
}
