package org.paradicms.lib.generic.stores

final class SparqlCollectionStoreSpec extends AbstractSparqlStoreSpec {
  "SPARQL store" should {
    val store = new SparqlCollectionStore with SparqlInstitutionStore {
      override protected val configuration: SparqlStoreConfiguration = configuration
    }
    ()

    "list institution collections" in {
      withUnknownHostExceptionCatch { () =>
        val collections = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = store.getInstitutions(currentUserUri = currentUserUri)(0).uri)
        collections.size should be > 0
      }
    }

    "get collection by URI" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val leftCollection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
        val rightCollection = store.getCollectionByUri(currentUserUri = currentUserUri, collectionUri = leftCollection.uri)
        leftCollection should equal(rightCollection)
      }
    }
  }
}
