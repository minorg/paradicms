package models.graphql

import org.paradicms.lib.generic.GenericTestData
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.test.FakeRequest
import sangria.ast.Document
import sangria.execution.Executor
import sangria.macros._
import sangria.marshalling.playJson._
import stores.TestGenericStore

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class GenericGraphQlSchemaDefinitionSpec extends PlaySpec {
  val testData = GenericTestData

  "GraphQL schema" must {
    "return a list of institutions" in {
      val query =
        graphql"""
         query InstitutionsQuery {
           institutions {
             name
             uri
           }
         }
       """
      val institutions = executeQuery(query).as[JsObject].value("data").result.get.as[JsObject].value("institutions").result.get.as[JsArray].value
      institutions.size must equal(1)
    }

    "return institution collections" in {
      val query =
        graphql"""
         query CollectionsQuery($$institutionUri: String!) {
           institutionByUri(uri: $$institutionUri) {
             collections {
               uri
             }
           }
         }
       """
      val collections = executeQuery(query, vars = Json.obj("institutionUri" -> testData.institution.uri.toString())).as[JsObject].value("data").result.get.as[JsObject].value("institutionByUri").result.get.as[JsObject].value("collections").result.get.as[JsArray].value
      collections.size must equal(1)
    }

    "return collection objects" in {
      val query =
        graphql"""
         query ObjectsQuery($$collectionUri: String!) {
           collectionByUri(uri: $$collectionUri) {
             objects(limit: 1, offset: 0, query: {}) {
               uri
             }
           }
         }
       """
      val objects = executeQuery(query, vars = Json.obj("collectionUri" -> testData.collection.uri.toString())).as[JsObject].value("data").result.get.as[JsObject].value("collectionByUri").result.get.as[JsObject].value.get("objects").get.as[JsObject].value.get("objects").get.as[JsArray].value
      objects.size must equal(1)
    }

    "return collection object facets" in {
      val query =
        graphql"""
         query ObjectsQuery($$collectionUri: String!) {
           collectionByUri(uri: $$collectionUri) {
             objectFacets {
               subjects
             }
           }
         }
       """
      val result = Json.stringify(executeQuery(query, vars = Json.obj("collectionUri" -> testData.collection.uri.toString())))
      result must include(testData.object_.subjects(0))
    }

    "return collection by URI" in {
      val query =
        graphql"""
         query CollectionByUriQuery($$collectionUri: String!) {
           collectionByUri(uri: $$collectionUri) {
               uri
           }
         }
       """
      executeQuery(query, vars = Json.obj("collectionUri" -> testData.collection.uri.toString())) must be(Json.parse(
        s"""
           |{"data":{"collectionByUri":{"uri":"${testData.collection.uri.toString()}"}}}
           |""".stripMargin))
    }

    "return institution by URI" in {
      val query =
        graphql"""
         query InstitutionByUriQuery($$institutionUri: String!) {
           institutionByUri(uri: $$institutionUri) {
               uri
           }
         }
       """
      executeQuery(query, vars = Json.obj("institutionUri" -> testData.institution.uri.toString())) must be(Json.parse(
        s"""
           |{"data":{"institutionByUri":{"uri":"${testData.institution.uri.toString()}"}}}
           |""".stripMargin))
    }

    "return object by URI" in {
      val query =
        graphql"""
         query ObjectByUriQuery($$objectUri: String!) {
           objectByUri(uri: $$objectUri) {
               uri
           }
         }
       """
      executeQuery(query, vars = Json.obj("objectUri" -> testData.object_.uri.toString())) must be(Json.parse(
        s"""
           |{"data":{"objectByUri":{"uri":"${testData.object_.uri.toString()}"}}}
           |""".stripMargin))
    }

    "search objects with text alone" in {
      val query =
        graphql"""
         query SearchObjectsQuery($$text: String!) {
           objects(limit: 10, offset: 0, query: { text: $$text }) {
               objectsWithContext {
                 object {
                     uri
                 }
               }
           }
         }
       """
      val result = Json.stringify(executeQuery(query, vars = Json.obj("text" -> testData.object_.title)))
      result must include(testData.object_.uri.toString())
    }

    "search objects with text and filters" in {
      val query =
        graphql"""
         query SearchObjectsQuery($$collectionUri: String!, $$text: String!) {
           objects(limit: 10, offset: 0, query: { filters: { collectionUris: { include: [$$collectionUri] } }, text: $$text }) {
               objectsWithContext {
                 object {
                     uri
                 }
               }
           }
         }
       """
      val result = Json.stringify(executeQuery(query, vars = Json.obj("collectionUri" -> testData.collection.uri.toString, "text" -> testData.object_.title)))
      result must include(testData.object_.uri.toString())
    }


  }

  def executeQuery(query: Document, vars: JsObject = Json.obj()) = {
    val futureResult = Executor.execute(GenericGraphQlSchemaDefinition.schema, query,
      variables = vars,
      userContext = new GenericGraphQlSchemaContext(FakeRequest(), new TestGenericStore())
    )
    Await.result(futureResult, 10.seconds)
  }
}

