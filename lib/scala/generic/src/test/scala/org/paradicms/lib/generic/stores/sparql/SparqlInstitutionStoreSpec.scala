package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.UnitSpec

final class SparqlInstitutionStoreSpec extends UnitSpec {

  private final class TestSparqlInstitutionStore extends TestSparqlStore with SparqlInstitutionStore

  "SPARQL store" should {
    val store = new TestSparqlInstitutionStore
    val currentUserUri = store.currentUserUri

    "list all institutions" in {
      val institutions = store.getInstitutions(currentUserUri = currentUserUri)
      institutions.size should be > 0
    }

    "get an institution by URI" in {
      val leftInstitution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val rightInstitution = store.getInstitutionByUri(currentUserUri = currentUserUri, institutionUri = leftInstitution.uri)
      leftInstitution should equal(rightInstitution)
    }
  }
}
