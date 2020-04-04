package models.graphql

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object}
import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaDefinition
import org.paradicms.lib.generic.stores.{GetObjectsResult, ObjectFacets, ObjectWithContext, ObjectsQuery}
import sangria.macros.derive._
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema.{Argument, Field, IntType, ListType, OptionType, Schema, StringType, fields}

final case class CollectionObjects(facets: ObjectFacets, objects: List[Object])

object GenericGraphQlSchemaDefinition extends AbstractGraphQlSchemaDefinition {
  // Object types, in dependence order
  implicit val ObjectType = deriveObjectType[GenericGraphQlSchemaContext, Object](
    AddFields(Field("thumbnail", OptionType(ImageType), resolve = _.value.images.find(image => image.thumbnail.isDefined).flatMap(image => image.thumbnail))),
  )
  implicit val ObjectWithContextType = deriveObjectType[GenericGraphQlSchemaContext, ObjectWithContext](
    ReplaceField("object_", Field("object", ObjectType, resolve = _.value.object_))
  )
  implicit val ObjectFacetsType = deriveObjectType[GenericGraphQlSchemaContext, ObjectFacets](
    ReplaceField("subjects", Field("subjects", ListType(StringType), resolve = _.value.subjects.toList)),
    ReplaceField("types", Field("types", ListType(StringType), resolve = _.value.types.toList))
  )
  val CollectionObjectsType = deriveObjectType[GenericGraphQlSchemaContext, CollectionObjects]()

  // https://github.com/sangria-graphql/sangria/issues/54
  implicit val CollectionType = deriveObjectType[GenericGraphQlSchemaContext, Collection](
    AddFields(
      Field(
        "objects",
        CollectionObjectsType,
        arguments = LimitArgument :: OffsetArgument :: Nil,
        resolve = ctx => {
          val result =
            ctx.ctx.store.getObjects(
              cachedCollectionsByUri = Map(ctx.value.uri -> ctx.value),
              currentUserUri = ctx.ctx.currentUserUri,
              limit = ctx.args.arg("limit").asInstanceOf[Integer],
              offset = ctx.args.arg("offset").asInstanceOf[Integer],
              query = ObjectsQuery.collection(ctx.value.uri)
            )
          CollectionObjects(
            facets = result.facets,
            objects = result.objectsWithContext.map(objectWithContext => objectWithContext.object_)
          )
        }
      ),
      Field(
        "objectsCount",
        IntType,
        resolve = ctx => ctx.ctx.store.getObjectsCount(currentUserUri = ctx.ctx.currentUserUri, query = ObjectsQuery.collection(ctx.value.uri))
      )
    )
  )

  implicit val InstitutionType = deriveObjectType[GenericGraphQlSchemaContext, Institution](
    AddFields(
      Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.args.arg("uri"))),
      Field("collections", ListType(CollectionType), resolve = ctx => ctx.ctx.store.getInstitutionCollections(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.value.uri))
    )
  )

  val GetObjectsResultType = deriveObjectType[GenericGraphQlSchemaContext, GetObjectsResult]()

  // Input types
  implicit val objectsQueryFromInput = new FromInput[ObjectsQuery] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      ObjectsQuery(
        collectionUri = ad.get("collectionUri").flatMap(_.asInstanceOf[Option[Uri]]),
        institutionUri = ad.get("institutionUri").flatMap(_.asInstanceOf[Option[Uri]]),
        text = ad.get("text").flatMap(_.asInstanceOf[Option[String]])
      )
    }
  }
  val ObjectsQueryInputType = deriveInputObjectType[ObjectsQuery]()

  // Argument types
  val ObjectsQueryArgument = Argument("query", ObjectsQueryInputType)

  // Query types
  val RootQueryType = sangria.schema.ObjectType("RootQuery", fields[GenericGraphQlSchemaContext, Unit](
    Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(collectionUri = ctx.args.arg("uri"), currentUserUri = ctx.ctx.currentUserUri)),
    Field("currentUser", OptionType(CurrentUserType), resolve = _.ctx.currentUser),
    Field("institutionByUri", InstitutionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getInstitutionByUri(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.args.arg("uri"))),
    Field("institutions", ListType(InstitutionType), resolve = ctx => ctx.ctx.store.getInstitutions(currentUserUri = ctx.ctx.currentUserUri)),
    Field("objects", GetObjectsResultType, arguments = LimitArgument :: OffsetArgument :: ObjectsQueryArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjects(currentUserUri = ctx.ctx.currentUserUri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"), query = ctx.args.arg("query"))),
    Field("objectsCount", IntType, arguments = ObjectsQueryArgument :: Nil, resolve = ctx => ctx.ctx.store.getObjectsCount(currentUserUri = ctx.ctx.currentUserUri, query = ctx.args.arg("query"))),
    Field("objectByUri", ObjectType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjectByUri(currentUserUri = ctx.ctx.currentUserUri, objectUri = ctx.args.arg("uri"))),
  ))

  // Schema
  val schema = Schema(
    RootQueryType
  )
}
