package models.graphql

import io.lemonlabs.uri.{Uri, Url}
import org.paradicms.lib.generic.models.domain.{Collection, DerivedImageSet, Image, Institution, Object, ObjectSearchResult, Rights, User}
import sangria.macros.derive._
import sangria.schema.{Argument, Field, IntType, ListType, OptionType, ScalarAlias, Schema, StringType, fields}

object GenericGraphQlSchemaDefinition {
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
  implicit val ImageType = deriveObjectType[GenericGraphQlSchemaContext, Image](
    ReplaceField("url", Field("url", UrlType, resolve = _.value.url))
  )

  implicit val RightsType = deriveObjectType[GenericGraphQlSchemaContext, Rights](
    ReplaceField("license", Field("license", OptionType(UriType), resolve = _.value.license))
  )

  implicit val DerivedImageSetType = deriveObjectType[GenericGraphQlSchemaContext, DerivedImageSet](
  )

  implicit val ObjectType = deriveObjectType[GenericGraphQlSchemaContext, Object](
    AddFields(Field("thumbnail", OptionType(ImageType), resolve = _.value.images.find(image => image.thumbnail.isDefined).flatMap(image => image.thumbnail))),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val CollectionType = deriveObjectType[GenericGraphQlSchemaContext, Collection](
    AddFields(
      Field(
        "objects",
        ListType(ObjectType),
        arguments = LimitArgument :: OffsetArgument :: Nil,
        resolve = ctx => ctx.ctx.store.getCollectionObjects(collectionUri = ctx.value.uri, currentUserUri = ctx.ctx.currentUserUri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"))
      ),
      Field(
        "objectsCount",
        IntType,
        resolve = ctx => ctx.ctx.store.getCollectionObjectsCount(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.value.uri)
      )
    ),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val InstitutionType = deriveObjectType[GenericGraphQlSchemaContext, Institution](
    AddFields(
      Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.args.arg("uri"))),
      Field("collections", ListType(CollectionType), resolve = ctx => ctx.ctx.store.getInstitutionCollections(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.value.uri))
    ),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val ObjectSearchResultType = deriveObjectType[GenericGraphQlSchemaContext, ObjectSearchResult](
    ReplaceField("object_", Field("object", ObjectType, resolve = _.value.object_))
  )

  // Intentionally limit the fields exposed on User
  implicit val CurrentUserType = sangria.schema.ObjectType("CurrentUser", fields[GenericGraphQlSchemaContext, User](
    Field("name", StringType, resolve = _.value.name)
  ))

  // Query types
  val RootQueryType = sangria.schema.ObjectType("RootQuery", fields[GenericGraphQlSchemaContext, Unit](
    Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(collectionUri = ctx.args.arg("uri"), currentUserUri = ctx.ctx.currentUserUri)),
    Field("currentUser", OptionType(CurrentUserType), resolve = _.ctx.currentUser),
    Field("institutionByUri", InstitutionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getInstitutionByUri(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.args.arg("uri"))),
    Field("institutions", ListType(InstitutionType), resolve = ctx => ctx.ctx.store.getInstitutions(currentUserUri = ctx.ctx.currentUserUri)),
    Field("matchingObjects", ListType(ObjectSearchResultType), arguments = LimitArgument :: OffsetArgument :: TextArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getMatchingObjects(currentUserUri = ctx.ctx.currentUserUri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"), text = ctx.args.arg("text"))),
    Field("matchingObjectsCount", IntType, arguments = TextArgument :: Nil, resolve = ctx => ctx.ctx.store.getMatchingObjectsCount(currentUserUri = ctx.ctx.currentUserUri, text = ctx.args.arg("text"))),
    Field("objectByUri", ObjectType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjectByUri(currentUserUri = ctx.ctx.currentUserUri, objectUri = ctx.args.arg("uri"))),
  ))

  // Schema
  val schema = Schema(
    RootQueryType
  )
}
