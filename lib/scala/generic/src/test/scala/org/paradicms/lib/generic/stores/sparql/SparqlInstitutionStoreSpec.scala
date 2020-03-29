package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.{GenericTestData, UnitSpec}

final class SparqlInstitutionStoreSpec extends UnitSpec {

  private final class TestSparqlInstitutionStore extends TestSparqlStore with SparqlInstitutionStore

  "SPARQL store" should {
    val store = new TestSparqlInstitutionStore
    val currentUserUri = store.currentUserUri
    val testData = GenericTestData

    "list all institutions" in {
      val institutions = store.getInstitutions(currentUserUri = currentUserUri).sortBy(institution => institution.uri.toString())
      institutions should equal(testData.institutions)
    }

    "get an institution by URI" in {
      val leftInstitution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val rightInstitution = store.getInstitutionByUri(currentUserUri = currentUserUri, institutionUri = leftInstitution.uri)
      leftInstitution should equal(rightInstitution)
    }
  }
}
