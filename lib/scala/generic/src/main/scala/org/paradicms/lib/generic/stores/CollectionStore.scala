package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Collection

trait CollectionStore {
  def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection

  def getCollectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection]

  def getInstitutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection]
}
