package org.paradicms.lib.test.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object, ObjectSearchResult, User}
import org.paradicms.lib.generic.stores.Store

class TestStore extends Store {
  private val testData = new GenericTestData

  override def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = if (collectionUri == testData.collection.uri) testData.collection else throw new NoSuchElementException

  override def getCollectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object] = if (offset == 0) List(testData.object_) else List()

  override def getCollectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = 1

  override def getInstitutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = if (institutionUri == testData.institution.uri) testData.institution else throw new NoSuchElementException

  override def getInstitutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection] = List(testData.collection)

  override def getInstitutions(currentUserUri: Option[Uri]): List[Institution] = List(testData.institution)

  override def getMatchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult] = if (offset == 0) List(new ObjectSearchResult(collection = testData.collection, institution = testData.institution, object_ = testData.object_)) else List()

  override def getMatchingObjectsCount(currentUserUri: Option[Uri], text: String) = 1

  override def getObjectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object = if (objectUri == testData.object_.uri) testData.object_ else throw new NoSuchElementException

  override def getUserByUri(userUri: Uri): Option[User] = if (userUri == testData.user.uri) Some(testData.user) else None

  override def putUser(user: User): Unit = throw new UnsupportedOperationException
}
