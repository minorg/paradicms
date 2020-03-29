package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.{GenericTestData, UnitSpec}

final class SparqlCollectionStoreSpec extends UnitSpec {

  private final class TestSparqlCollectionStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore

  "SPARQL store" should {
    val store = new TestSparqlCollectionStore
    val currentUserUri = store.currentUserUri
    val testData = GenericTestData

    "list institution collections" in {
      val collections = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = testData.institution.uri).sortBy(collection => collection.uri.toString())
      collections should equal(testData.collections)
    }

    "get collection by URI" in {
      val collection = store.getCollectionByUri(collectionUri = testData.collection.uri, currentUserUri = currentUserUri)
      collection should equal(testData.collection)
    }
  }
}
