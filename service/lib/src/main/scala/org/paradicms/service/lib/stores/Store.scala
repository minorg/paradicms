package org.paradicms.service.lib.stores

import io.lemonlabs.uri.Uri
import org.paradicms.service.lib.models.domain.{Collection, Institution, Object, ObjectSearchResult, User}

trait Store {
  def collectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection

  def collectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object]

  def collectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int

  def institutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution

  def institutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection]

  def institutions(currentUserUri: Option[Uri]): List[Institution]

  def matchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult]

  def matchingObjectsCount(currentUserUri: Option[Uri], text: String): Int

  def objectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object

  def userByUri(userUri: Uri): Option[User]
}
