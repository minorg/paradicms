package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Object

trait ObjectStore {
  def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): CollectionObjects

  def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int

  def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): MatchingObjects

  def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String): Int

  def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object
}
