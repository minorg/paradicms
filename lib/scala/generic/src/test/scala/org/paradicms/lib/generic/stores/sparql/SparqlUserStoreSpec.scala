package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.{GenericTestData, UnitSpec}

final class SparqlUserStoreSpec extends UnitSpec {

  private final class TestSparqlUserStore extends TestSparqlStore with SparqlUserStore

  "SPARQL store" should {
    val store = new TestSparqlUserStore
    val testData = GenericTestData

    "put and get a user" in {
      store.putUser(testData.user)
      store.getUserByUri(testData.user.uri).get should equal(testData.user)
    }
  }
}
