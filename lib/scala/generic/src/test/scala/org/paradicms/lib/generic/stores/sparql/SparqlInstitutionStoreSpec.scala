package org.paradicms.lib.generic.stores.sparql

final class SparqlInstitutionStoreSpec extends AbstractSparqlStoreSpec {

  private final class TestSparqlInstitutionStore(protected val configuration: SparqlStoreConfiguration) extends SparqlInstitutionStore

  "SPARQL store" should {
    val store = new TestSparqlInstitutionStore(configuration)

    "list all institutions" in {
      withUnknownHostExceptionCatch { () =>
        val institutions = store.getInstitutions(currentUserUri = currentUserUri)
        institutions.size should be > 0
      }
    }

    "get an institution by URI" in {
      withUnknownHostExceptionCatch { () =>
        val leftInstitution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val rightInstitution = store.getInstitutionByUri(currentUserUri = currentUserUri, institutionUri = leftInstitution.uri)
        leftInstitution should equal(rightInstitution)
      }
    }
  }
}
