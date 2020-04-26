package models.graphql

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object}
import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaDefinition
import org.paradicms.lib.generic.stores._
import sangria.macros.derive._
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema.{Argument, Field, IntType, ListType, OptionInputType, OptionType, Schema, fields}

object GenericGraphQlSchemaDefinition extends AbstractGraphQlSchemaDefinition {
  // Input types
  implicit val stringFacetFilterFromInput = new FromInput[StringFacetFilter] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]
      StringFacetFilter(
        exclude = ad.get("exclude").flatMap(_.asInstanceOf[Option[Vector[String]]]).map(_.toList),
        include = ad.get("include").flatMap(_.asInstanceOf[Option[Vector[String]]]).map(_.toList)
      )
    }
  }
  implicit val stringFacetFilterType = deriveInputObjectType[StringFacetFilter]()

  implicit val uriFacetFilterFromInput = new FromInput[UriFacetFilter] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]
      UriFacetFilter(
        exclude = ad.get("exclude").flatMap(_.asInstanceOf[Option[Vector[Uri]]]).map(_.toList),
        include = ad.get("include").flatMap(_.asInstanceOf[Option[Vector[Uri]]]).map(_.toList)
      )
    }
  }
  implicit val uriFacetFilterType = deriveInputObjectType[UriFacetFilter]()

  implicit val objectFiltersFromInput = new FromInput[ObjectFilters] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]
      def stringFacetFilterFromValue(key: String) =
        ad.get(key).flatMap(_.asInstanceOf[Option[Map[String, Any]]]).map(node => stringFacetFilterFromInput.fromResult(node))
      def uriFacetFilterFromValue(key: String) =
        ad.get(key).flatMap(_.asInstanceOf[Option[Map[String, Any]]]).map(node => uriFacetFilterFromInput.fromResult(node))
      ObjectFilters(
        collectionUris = uriFacetFilterFromValue("collectionUris"),
        culturalContexts = stringFacetFilterFromValue("culturalContexts"),
        institutionUris = uriFacetFilterFromValue("institutionUris"),
        materials = stringFacetFilterFromValue("materials"),
        spatials = stringFacetFilterFromValue("spatials"),
        subjects = stringFacetFilterFromValue("subjects"),
        techniques = stringFacetFilterFromValue("techniques"),
        temporals = stringFacetFilterFromValue("temporals"),
        types = stringFacetFilterFromValue("types")
      )
    }
  }
  implicit val ObjectFiltersType = deriveInputObjectType[ObjectFilters]()

  implicit val objectQueryFromInput = new FromInput[ObjectQuery] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]
      ObjectQuery(
        filters = ad.get("filters").flatMap(_.asInstanceOf[Option[Map[String, Any]]]).map(node => objectFiltersFromInput.fromResult(node)),
        text = ad.get("text").flatMap(_.asInstanceOf[Option[String]])
      )
    }
  }
  implicit val ObjectQueryType = deriveInputObjectType[ObjectQuery]()

  // Argument types
  val ObjectQueryArgument = Argument("query", ObjectQueryType)
  val OptionalObjectQueryArgument = Argument("query", OptionInputType(ObjectQueryType))

  // Object types, in dependence order
  implicit val ObjectType = deriveObjectType[GenericGraphQlSchemaContext, Object](
    AddFields(Field("thumbnail", OptionType(ImageType), resolve = _.value.images.find(image => image.thumbnail.isDefined).flatMap(image => image.thumbnail))),
  )
  implicit val ObjectWithContextType = deriveObjectType[GenericGraphQlSchemaContext, ObjectWithContext](
    ReplaceField("object_", Field("object", ObjectType, resolve = _.value.object_))
  )
  implicit val ObjectFacetsType = deriveObjectType[GenericGraphQlSchemaContext, ObjectFacets]()

  private def validateCollectionObjectsQuery(collectionUri: Uri, query: Option[ObjectQuery]): ObjectQuery =
    if (query.isDefined)
      validateCollectionObjectsQuery(collectionUri, query.get)
    else
      ObjectQuery.collection(collectionUri)

  private def validateCollectionObjectsQuery(collectionUri: Uri, query: ObjectQuery): ObjectQuery =
    if (!query.filters.isDefined) {
      query.copy(filters = Some(ObjectFilters.collection(collectionUri)))
    } else if (!query.filters.get.collectionUris.isDefined) {
      throw new IllegalStateException("query has filters but none on collection")
    } else if (!query.filters.get.collectionUris.get.include.isDefined) {
      throw new IllegalStateException("query has filters but none including collection")
    } else if (!query.filters.get.collectionUris.get.include.get.contains(collectionUri)) {
      throw new IllegalStateException("query has filters but collection include does not have current collection URI");
    } else {
      query
    }

  // https://github.com/sangria-graphql/sangria/issues/54
  implicit val CollectionType = deriveObjectType[GenericGraphQlSchemaContext, Collection](
    AddFields(
      Field(
        "objectFacets",
        ObjectFacetsType,
        // Doesn't take arguments, always returns facets for the whole collection
        resolve = ctx =>
          ctx.ctx.store.getObjectFacets(
            cachedCollectionsByUri = Map(ctx.value.uri -> ctx.value),
            currentUserUri = ctx.ctx.currentUserUri,
            query = ObjectQuery.collection(ctx.value.uri)
          ).facets
      ),
      Field(
        "objects",
        ListType(ObjectType),
        arguments = LimitArgument :: OffsetArgument :: OptionalObjectQueryArgument :: Nil,
        resolve = ctx =>
          ctx.ctx.store.getObjects(
            cachedCollectionsByUri = Map(ctx.value.uri -> ctx.value),
            currentUserUri = ctx.ctx.currentUserUri,
            limit = ctx.args.arg("limit").asInstanceOf[Integer],
            offset = ctx.args.arg("offset").asInstanceOf[Integer],
            query = validateCollectionObjectsQuery(ctx.value.uri, ctx.args.argOpt("query"))
          ).objectsWithContext.map(objectWithContext => objectWithContext.object_)
      ),
      Field(
        "objectsCount",
        IntType,
        arguments = OptionalObjectQueryArgument :: Nil,
        resolve = ctx =>
          ctx.ctx.store.getObjectsCount(
            currentUserUri = ctx.ctx.currentUserUri,
            query = validateCollectionObjectsQuery(ctx.value.uri, ctx.args.argOpt("query"))
        )
      )
    )
  )

  implicit val InstitutionType = deriveObjectType[GenericGraphQlSchemaContext, Institution](
    AddFields(
      Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(currentUserUri = ctx.ctx.currentUserUri, collectionUri = ctx.args.arg("uri"))),
      Field("collections", ListType(CollectionType), resolve = ctx => ctx.ctx.store.getInstitutionCollections(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.value.uri))
    )
  )

  val GetObjectFacetsResultType = deriveObjectType[GenericGraphQlSchemaContext, GetObjectFacetsResult]()
  val GetObjectsResultType = deriveObjectType[GenericGraphQlSchemaContext, GetObjectsResult]()

  // Query types
  val RootQueryType = sangria.schema.ObjectType("RootQuery", fields[GenericGraphQlSchemaContext, Unit](
    Field("collectionByUri", CollectionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getCollectionByUri(collectionUri = ctx.args.arg("uri"), currentUserUri = ctx.ctx.currentUserUri)),
    Field("currentUser", OptionType(CurrentUserType), resolve = _.ctx.currentUser),
    Field("institutionByUri", InstitutionType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getInstitutionByUri(currentUserUri = ctx.ctx.currentUserUri, institutionUri = ctx.args.arg("uri"))),
    Field("institutions", ListType(InstitutionType), resolve = ctx => ctx.ctx.store.getInstitutions(currentUserUri = ctx.ctx.currentUserUri)),
    Field("objectFacets", GetObjectFacetsResultType, arguments = ObjectQueryArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjectFacets(currentUserUri = ctx.ctx.currentUserUri, query = ctx.args.arg("query"))),
    Field("objects", GetObjectsResultType, arguments = LimitArgument :: OffsetArgument :: ObjectQueryArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjects(currentUserUri = ctx.ctx.currentUserUri, limit = ctx.args.arg("limit"), offset = ctx.args.arg("offset"), query = ctx.args.arg("query"))),
    Field("objectsCount", IntType, arguments = ObjectQueryArgument :: Nil, resolve = ctx => ctx.ctx.store.getObjectsCount(currentUserUri = ctx.ctx.currentUserUri, query = ctx.args.arg("query"))),
    Field("objectByUri", ObjectType, arguments = UriArgument :: Nil, resolve = (ctx) => ctx.ctx.store.getObjectByUri(currentUserUri = ctx.ctx.currentUserUri, objectUri = ctx.args.arg("uri"))),
  ))

  // Schema
  val schema = Schema(
    RootQueryType
  )
}
