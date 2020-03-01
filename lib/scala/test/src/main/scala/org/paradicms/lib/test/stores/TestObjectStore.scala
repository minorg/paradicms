package org.paradicms.lib.test.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Object, ObjectSearchResult}
import org.paradicms.lib.generic.stores.ObjectStore

trait TestObjectStore extends ObjectStore {
  protected val testData: GenericTestData

  override def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object] = if (offset == 0) List(testData.object_) else List()

  override def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = 1

  override def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult] = if (offset == 0) List(new ObjectSearchResult(collection = testData.collection, institution = testData.institution, object_ = testData.object_)) else List()

  override def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String) = 1

  override def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object = if (objectUri == testData.object_.uri) testData.object_ else throw new NoSuchElementException
}
