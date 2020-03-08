package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Object, ObjectSearchResult}

trait ObjectStore {
  def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object]

  def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int

  def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult]

  def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String): Int

  def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object
}
