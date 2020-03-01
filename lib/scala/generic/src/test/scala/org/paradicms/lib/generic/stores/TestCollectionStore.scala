package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Collection

trait TestCollectionStore extends CollectionStore {
  protected val testData: GenericTestData

  override def getCollectionsByUris(collectionUris: List[Uri], currentUserUri: Option[Uri]): List[Collection] = {
    if (collectionUris.isEmpty) {
      List()
    } else if (collectionUris.size == 1) {
      if (collectionUris(0) == testData.collection.uri) {
        List(testData.collection)
      } else {
        throw new NoSuchElementException
      }
    } else {
      throw new NoSuchElementException
    }
  }

  override def getCollectionByUri(collectionUri: Uri, currentUserUri: Option[Uri]): Collection = if (collectionUri == testData.collection.uri) testData.collection else throw new NoSuchElementException

  override def getInstitutionCollections(currentUserUri: Option[Uri], institutionUri: Uri): List[Collection] = List(testData.collection)
}
