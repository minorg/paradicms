package org.paradicms.lib.generic.models.graphql

import org.paradicms.lib.generic.models.domain._
import sangria.macros.derive._
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema.{Argument, Field, IntType, OptionType, StringType, fields}

abstract class AbstractGraphQlSchemaDefinition {
  implicit val uriType = UriType

  // Scalar argument types
  val LimitArgument = Argument("limit", IntType, description = "Limit")
  val OffsetArgument = Argument("offset", IntType, description = "Offset")
  val UriArgument = Argument("uri", UriType, description = "URI")

  // Complex input types
  implicit val imageDimensionsFromInput = new FromInput[ImageDimensions] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]
      ImageDimensions(
        height = ad("height").asInstanceOf[Number].intValue(),
        width = ad("width").asInstanceOf[Number].intValue()
      )
    }
  }
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

  // Rights
  implicit val RightsType = deriveObjectType[Unit, Rights]()
}
