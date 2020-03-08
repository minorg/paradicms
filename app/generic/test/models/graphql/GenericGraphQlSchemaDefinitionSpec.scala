package models.graphql

import org.paradicms.lib.generic.stores.GenericTestData
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.test.FakeRequest
import sangria.ast.Document
import sangria.execution.Executor
import sangria.macros._
import sangria.marshalling.playJson._
import stores.test.TestGenericStore

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class GenericGraphQlSchemaDefinitionSpec extends PlaySpec {
  val testData = new GenericTestData

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
             objects(limit: 1, offset: 0) {
               uri
             }
           }
         }
       """
      val objects = executeQuery(query, vars = Json.obj("collectionUri" -> testData.collection.uri.toString())).as[JsObject].value("data").result.get.as[JsObject].value("collectionByUri").result.get.as[JsObject].value.get("objects").get.as[JsArray].value
      objects.size must equal(1)
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

    "search objects" in {
      val query =
        graphql"""
         query SearchObjectsQuery($$text: String!) {
           matchingObjects(limit: 10, offset: 0, text: $$text) {
               object {
                   uri
               }
           }
         }
       """
      executeQuery(query, vars = Json.obj("text" -> "irrelevant")) must be(Json.parse(
        s"""
           |{"data":{"matchingObjects":[{"object":{"uri":"${testData.object_.uri.toString()}"}}]}}
           |""".stripMargin))
    }


    ////    "allow to fetch Han Solo using his ID provided through variables" in {
    ////      val query =
    ////        graphql"""
    ////         query FetchSomeIDQuery($$humanId: String!) {
    ////           human(id: $$humanId) {
    ////             name
    ////             friends {
    ////               id
    ////               name
    ////             }
    ////           }
    ////         }
    ////       """
    ////
    ////      executeQuery(query, vars = Json.obj("humanId" → JsString("1002"))) must be (Json.parse(
    ////        """
    ////         {
    ////           "data": {
    ////             "human": {
    ////               "name": "Han Solo",
    ////               "friends": [
    ////                 {
    ////                   "id": "1000",
    ////                   "name": "Luke Skywalker"
    ////                 },
    ////                 {
    ////                   "id": "1003",
    ////                   "name": "Leia Organa"
    ////                 },
    ////                 {
    ////                   "id": "2001",
    ////                   "name": "R2-D2"
    ////                 }
    ////               ]
    ////             }
    ////           }
    ////         }
    ////        """))
    //    }
  }

  def executeQuery(query: Document, vars: JsObject = Json.obj()) = {
    val futureResult = Executor.execute(GenericGraphQlSchemaDefinition.schema, query,
      variables = vars,
      userContext = new GenericGraphQlSchemaContext(FakeRequest(), new TestGenericStore())
    )
    Await.result(futureResult, 10.seconds)
  }
}

