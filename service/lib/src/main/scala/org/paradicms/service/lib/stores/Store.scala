package org.paradicms.service.lib.stores

import io.lemonlabs.uri.Uri
import org.paradicms.service.lib.models.domain.{Collection, Institution, Object, ObjectSearchResult, User}

trait Store {
  def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection

  def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object]

  def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int

  def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution

  def getInstitutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection]

  def getInstitutions(currentUserUri: Option[Uri]): List[Institution]

  def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult]

  def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String): Int

  def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object

  def getUserByUri(userUri: Uri): Option[User]
}
