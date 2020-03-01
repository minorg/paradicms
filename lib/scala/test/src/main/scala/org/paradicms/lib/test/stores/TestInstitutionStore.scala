package org.paradicms.lib.test.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Institution
import org.paradicms.lib.generic.stores.InstitutionStore

trait TestInstitutionStore extends InstitutionStore {
  protected val testData: GenericTestData

  override def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = if (institutionUri == testData.institution.uri) testData.institution else throw new NoSuchElementException

  override def getInstitutions(currentUserUri: Option[Uri]): List[Institution] = List(testData.institution)

  override def getInstitutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution] = {
    if (institutionUris.isEmpty) {
      List()
    } else if (institutionUris.size == 1) {
      if (institutionUris(0) == testData.institution.uri) {
        List(testData.institution)
      } else {
        throw new NoSuchElementException
      }
    } else {
      throw new NoSuchElementException
    }
  }
}
