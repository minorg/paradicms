package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.{GenericTestData, UnitSpec}

import scala.collection.JavaConverters._

final class SparqlUserStoreSpec extends UnitSpec {

  private final class TestSparqlUserStore extends TestSparqlStore with SparqlUserStore {
    def isEmpty = dataset.isEmpty
    def removeAll(): Unit = {
      dataset.getDefaultModel.removeAll()
      for (modelName <- dataset.listNames().asScala.toList) {
        dataset.removeNamedModel(modelName)
      }
    }
  }

  "SPARQL store" should {
    val store = new TestSparqlUserStore
    val testData = GenericTestData

    "put and get a user" in {
      store.removeAll()
      store.isEmpty should be(true)
      store.getUserByUri(testData.user.uri) should equal(None)
      store.putUser(testData.user)
      store.getUserByUri(testData.user.uri) should equal(Some(testData.user))
    }
  }
}
