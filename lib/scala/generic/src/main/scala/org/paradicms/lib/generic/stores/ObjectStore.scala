package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Object}

trait ObjectStore {
  def getObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, query: ObjectsQuery, cachedCollectionsByUri: Map[Uri, Collection] = Map()): GetObjectsResult

  def getObjectsCount(currentUserUri: Option[Uri], query: ObjectsQuery): Int

  def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object
}
