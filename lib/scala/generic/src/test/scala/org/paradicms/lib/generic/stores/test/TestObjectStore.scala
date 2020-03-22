package org.paradicms.lib.generic.stores.test

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Object
import org.paradicms.lib.generic.stores.{CollectionObjects, GenericTestData, MatchingObject, MatchingObjects, ObjectFacets, ObjectStore}

trait TestObjectStore extends ObjectStore {
  protected val testData: GenericTestData

  override def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): CollectionObjects =
    CollectionObjects(
      facets = testData.objectFacets,
      objects = if (offset == 0) List(testData.object_) else List()
    )

  override def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = 1

  override def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): MatchingObjects =
    MatchingObjects(
      collectionsByUri = Map(testData.collection.uri -> testData.collection),
      facets = testData.objectFacets,
      institutionsByUri = Map(testData.institution.uri -> testData.institution),
      objects =   if (offset == 0) List(new MatchingObject(collectionUri = testData.collection.uri, institutionUri = testData.institution.uri, object_ = testData.object_)) else List()
    )

  override def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String) = 1

  override def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object = if (objectUri == testData.object_.uri) testData.object_ else throw new NoSuchElementException
}
