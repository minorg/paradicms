package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object}

trait ObjectStore {
  def getObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, query: ObjectQuery, cachedCollectionsByUri: Map[Uri, Collection] = Map(), cachedInstitutionsByuri: Map[Uri, Institution] = Map()): GetObjectsResult

  def getObjectFacets(currentUserUri: Option[Uri], query: ObjectQuery, cachedCollectionsByUri: Map[Uri, Collection] = Map(), cachedInstitutionsByUri: Map[Uri, Institution] = Map()): GetObjectFacetsResult

  def getObjectsCount(currentUserUri: Option[Uri], query: ObjectQuery): Int

  def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object
}
