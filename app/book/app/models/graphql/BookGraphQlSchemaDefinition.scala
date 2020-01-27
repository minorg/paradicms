package models.graphql

import org.paradicms.lib.generic.models.domain.{Collection, DerivedImageSet, Image, Institution, Object, ObjectSearchResult, Rights}
import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaDefinition
import sangria.macros.derive._
import sangria.schema.{Field, IntType, ListType, OptionType, Schema, fields}

object BookGraphQlSchemaDefinition extends AbstractGraphQlSchemaDefinition {
  // Domain model types, in dependence order
  implicit val ImageType = deriveObjectType[BookGraphQlSchemaContext, Image](
    ReplaceField("url", Field("url", UrlType, resolve = _.value.url))
  )

  implicit val RightsType = deriveObjectType[BookGraphQlSchemaContext, Rights](
    ReplaceField("license", Field("license", OptionType(UriType), resolve = _.value.license))
  )

  implicit val DerivedImageSetType = deriveObjectType[BookGraphQlSchemaContext, DerivedImageSet](
  )

  implicit val ObjectType = deriveObjectType[BookGraphQlSchemaContext, Object](
    AddFields(Field("thumbnail", OptionType(ImageType), resolve = _.value.images.find(image => image.thumbnail.isDefined).flatMap(image => image.thumbnail))),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val CollectionType = deriveObjectType[BookGraphQlSchemaContext, Collection](
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

  implicit val InstitutionType = deriveObjectType[BookGraphQlSchemaContext, Institution](
    AddFields(
      Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.args.arg("uri"))),
      Field("collections", ListType(CollectionType), resolve = ctx => ctx.ctx.store.getInstitutionCollections(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.value.uri))
    ),
    ReplaceField("uri", Field("uri", UriType, resolve = _.value.uri))
  )

  implicit val ObjectSearchResultType = deriveObjectType[BookGraphQlSchemaContext, ObjectSearchResult](
    ReplaceField("object_", Field("object", ObjectType, resolve = _.value.object_))
  )

  // Query types
  val RootQueryType = sangria.schema.ObjectType("RootQuery", fields[BookGraphQlSchemaContext, Unit](
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
