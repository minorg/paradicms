package models.graphql

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object}
import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaDefinition
import org.paradicms.lib.generic.stores.{CollectionObjects, MatchingObject, MatchingObjects, ObjectFacets}
import sangria.macros.derive._
import sangria.schema.{Field, IntType, ListType, OptionType, ScalarAlias, Schema, StringType, fields}

object GenericGraphQlSchemaDefinition extends AbstractGraphQlSchemaDefinition {
  // Domain model types, in dependence order
  implicit val ObjectType = deriveObjectType[GenericGraphQlSchemaContext, Object](
    AddFields(Field("thumbnail", OptionType(ImageType), resolve = _.value.images.find(image => image.thumbnail.isDefined).flatMap(image => image.thumbnail))),
  )

  implicit val ObjectFacetsType = deriveObjectType[GenericGraphQlSchemaContext, ObjectFacets]()
  implicit val CollectionObjectsType = deriveObjectType[GenericGraphQlSchemaContext, CollectionObjects]()

  implicit val CollectionType = deriveObjectType[GenericGraphQlSchemaContext, Collection](
    AddFields(
      Field(
        "objects",
        CollectionObjectsType,
        arguments = LimitArgument :: OffsetArgument :: Nil,
        resolve = ctx => ctx.ctx.store.getCollectionObjects(collectionUri = ctx.value.uri, currentUserUri = ctx.ctx.currentUserUri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"))
      ),
      Field(
        "objectsCount",
        IntType,
        resolve = ctx => ctx.ctx.store.getCollectionObjectsCount(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.value.uri)
      )
    )
  )

  implicit val InstitutionType = deriveObjectType[GenericGraphQlSchemaContext, Institution](
    AddFields(
      Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.args.arg("uri"))),
      Field("collections", ListType(CollectionType), resolve = ctx => ctx.ctx.store.getInstitutionCollections(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.value.uri))
    )
  )

  implicit val MatchingObjectType = deriveObjectType[GenericGraphQlSchemaContext, MatchingObject](
    ReplaceField("object_", Field("object", ObjectType, resolve = _.value.object_))
  )
  implicit val MatchingObjectsType = deriveObjectType[GenericGraphQlSchemaContext, MatchingObjects]()

  // Query types
  val RootQueryType = sangria.schema.ObjectType("RootQuery", fields[GenericGraphQlSchemaContext, Unit](
    Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(collectionUri = ctx.args.arg("uri"), currentUserUri = ctx.ctx.currentUserUri)),
    Field("currentUser", OptionType(CurrentUserType), resolve = _.ctx.currentUser),
    Field("institutionByUri", InstitutionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getInstitutionByUri(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.args.arg("uri"))),
    Field("institutions", ListType(InstitutionType), resolve = ctx => ctx.ctx.store.getInstitutions(currentUserUri = ctx.ctx.currentUserUri)),
    Field("matchingObjects", MatchingObjectsType, arguments = LimitArgument :: OffsetArgument :: TextArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getMatchingObjects(currentUserUri = ctx.ctx.currentUserUri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"), text = ctx.args.arg("text"))),
    Field("matchingObjectsCount", IntType, arguments = TextArgument :: Nil, resolve = ctx => ctx.ctx.store.getMatchingObjectsCount(currentUserUri = ctx.ctx.currentUserUri, text = ctx.args.arg("text"))),
    Field("objectByUri", ObjectType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjectByUri(currentUserUri = ctx.ctx.currentUserUri, objectUri = ctx.args.arg("uri"))),
  ))

  // Schema
  val schema = Schema(
    RootQueryType
  )
}
