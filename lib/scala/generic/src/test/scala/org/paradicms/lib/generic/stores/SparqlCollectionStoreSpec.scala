package org.paradicms.lib.generic.stores

final class SparqlCollectionStoreSpec extends AbstractSparqlStoreSpec {

  private final class TestSparqlCollectionStore(protected val configuration: SparqlStoreConfiguration) extends SparqlCollectionStore with SparqlInstitutionStore

  "SPARQL store" should {
    val store = new TestSparqlCollectionStore(configuration)

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
