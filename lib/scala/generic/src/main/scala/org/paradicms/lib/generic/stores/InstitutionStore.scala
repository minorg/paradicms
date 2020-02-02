package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Institution

trait InstitutionStore {
  def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution

  def getInstitutions(currentUserUri: Option[Uri]): List[Institution]

  def getInstitutionsByUris(currentUserUri: Option[Uri], institutionUris: List[Uri]): List[Institution]
}
