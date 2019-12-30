package stores

import io.lemonlabs.uri.Uri
import org.paradicms.service.lib.models.domain.{Collection, Institution, Object, ObjectSearchResult}
import org.paradicms.service.lib.stores.Store

object TestStore extends Store {
  override def collectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = if (collectionUri == TestData.collection.uri) TestData.collection else throw new NoSuchElementException

  override def collectionObjects(collectionUri: Uri, currentUserUri: Option[Uri], limit: Int, offset: Int): List[Object] = if (offset == 0) List(TestData.object_) else List()

  override def collectionObjectsCount(collectionUri: Uri, currentUserUri: Option[Uri]): Int = 1

  override def institutionByUri(currentUserUri: Option[Uri], institutionUri: Uri): Institution = if (institutionUri == TestData.institution.uri) TestData.institution else throw new NoSuchElementException

  override def institutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection] = List(TestData.collection)

  override def institutions(currentUserUri: Option[Uri]): List[Institution] = List(TestData.institution)

  override def matchingObjects(currentUserUri: Option[Uri], limit: Int, offset: Int, text: String): List[ObjectSearchResult] = if (offset == 0) List(new ObjectSearchResult(collection = TestData.collection, institution = TestData.institution, object_ = TestData.object_)) else List()

  override def matchingObjectsCount(currentUserUri: Option[Uri], text: String) = 1

  override def objectByUri(currentUserUri: Option[Uri], objectUri: Uri): Object = if (objectUri == TestData.object_.uri) TestData.object_ else throw new NoSuchElementException
}
