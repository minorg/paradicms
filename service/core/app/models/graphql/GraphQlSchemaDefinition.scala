package models.graphql

import io.lemonlabs.uri.{Uri, Url}
import models.domain.{Collection, DerivedImageSet, Image, Institution, Object, ObjectSearchResult, Rights}
import sangria.macros.derive._
import sangria.schema.{Argument, Field, IntType, ListType, OptionType, ScalarAlias, Schema, StringType, fields}

object GraphQlSchemaDefinition {
  // Scalar aliases
  implicit val UriType = ScalarAlias[Uri, String](
    StringType, _.toString, uri => Right(Uri.parse(uri))
  )

  implicit val UrlType = ScalarAlias[Url, String](
    StringType, _.toString, uri => Right(Url.parse(uri))
  )

  // Scalar argument types
  val LimitArgument = Argument("limit", IntType, description = "Limit")
  val OffsetArgument = Argument("offset", IntType, description = "Offset")
  val TextArgument = Argument("text", StringType, description = "Text")
  val UriArgument = Argument("uri", UriType, description = "URI")

  // Domain model types, in dependence order
  implicit val ImageType = deriveObjectType[GraphQlSchemaContext, Image](
    ReplaceField("url", Field("url", UrlType, resolve = _.value.url))
  )

  implicit val RightsType = deriveObjectType[GraphQlSchemaContext, Rights](
    ReplaceField("license", Field("license", OptionType(UriType), resolve = _.value.license))
  )

  implicit val DerivedImageSetType = deriveObjectType[GraphQlSchemaContext, DerivedImageSet](
  )

  implicit val ObjectType = deriveObjectType[GraphQlSchemaContext, Object](
    AddFields(Field("thumbnail", OptionType(ImageType), resolve = _.value.images.find(image => image.thumbnail.isDefined).flatMap(image => image.thumbnail))),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val CollectionType = deriveObjectType[GraphQlSchemaContext, Collection](
    AddFields(
      Field(
        "objects",
        ListType(ObjectType),
        arguments = LimitArgument :: OffsetArgument :: Nil,
        resolve = ctx => ctx.ctx.store.collectionObjects(collectionUri = ctx.value.uri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"))
      ),
      Field(
        "objectsCount",
        IntType,
        resolve = ctx => ctx.ctx.store.collectionObjectsCount(collectionUri = ctx.value.uri)
      )
    ),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val InstitutionType = deriveObjectType[GraphQlSchemaContext, Institution](
    AddFields(
      Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.collectionByUri(ctx.args.arg("uri"))),
      Field("collections", ListType(CollectionType), resolve = ctx => ctx.ctx.store.institutionCollections(ctx.value.uri))
    ),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val ObjectSearchResultType = deriveObjectType[GraphQlSchemaContext, ObjectSearchResult](
    ReplaceField("object_", Field("object", ObjectType, resolve = _.value.object_))
  )

  // Query types
  val RootQueryType = sangria.schema.ObjectType("RootQuery", fields[GraphQlSchemaContext, Unit](
    Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.collectionByUri(ctx.args.arg("uri"))),
    Field("institutionByUri", InstitutionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.institutionByUri(ctx.args.arg("uri"))),
    Field("institutions", ListType(InstitutionType), resolve = _.ctx.store.institutions),
    Field("matchingObjects", ListType(ObjectSearchResultType), arguments = LimitArgument :: OffsetArgument :: TextArgument :: Nil, resolve = (ctx) => ctx.ctx.store.matchingObjects(limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"), text = ctx.args.arg("text"))),
    Field("matchingObjectsCount", IntType, arguments = TextArgument :: Nil, resolve = ctx => ctx.ctx.store.matchingObjectsCount(ctx.args.arg("text"))),
    Field("objectByUri", ObjectType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.objectByUri(ctx.args.arg("uri"))),
  ))

  // Schema
  val schema = Schema(
    RootQueryType
  )
}
