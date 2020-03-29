package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.UnitSpec

final class SparqlCollectionStoreSpec extends UnitSpec {

  private final class TestSparqlCollectionStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore

  "SPARQL store" should {
    val store = new TestSparqlCollectionStore
    val currentUserUri = store.currentUserUri

    "list institution collections" in {
      val collections = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = store.getInstitutions(currentUserUri = currentUserUri)(0).uri)
      collections.size should be > 0
    }

    "get collection by URI" in {
      val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val leftCollection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
      val rightCollection = store.getCollectionByUri(currentUserUri = currentUserUri, collectionUri = leftCollection.uri)
      leftCollection should equal(rightCollection)
    }
  }
}
