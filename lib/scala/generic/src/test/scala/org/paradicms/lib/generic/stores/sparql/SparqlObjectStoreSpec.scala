package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.{GenericTestData, UnitSpec}

final class SparqlObjectStoreSpec extends UnitSpec {

  private final class TestSparqlObjectStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore

  "SPARQL store" should {
    val store = new TestSparqlObjectStore
    val currentUserUri = store.currentUserUri
    val testData = GenericTestData

    "list collection objects" in {
      val objects = store.getCollectionObjects(collectionUri = testData.collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 0).objects.sortBy(object_ => object_.uri.toString())
      objects should equal(testData.objects)
//      val objectWithImages = objects.find(object_ => !object_.images.isEmpty)
//      objectWithImages should not be (null)
//      val objectWithThumbnail = objects.find(object_ => object_.images.exists(image => image.thumbnail.isDefined))
//      objectWithThumbnail should not be (null)
//      objects.size should be(10)
//      val nextObjects = store.getCollectionObjects(collectionUri = testData.collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 10).objects
//      nextObjects.size should be(10)
//      nextObjects.map(object_ => object_.uri).toSet.intersect(objects.map(object_ => object_.uri).toSet).size should be(0)
    }

    "get collection object facets" in {
      val facets = store.getCollectionObjects(collectionUri = testData.collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 0).facets
      val actualSubjects = facets.subjects.toList.sortBy(subject => subject)
      val expectedSubjects = testData.objects.flatMap(object_ => object_.subjects).toSet.toList.sorted
      actualSubjects should equal(expectedSubjects)
    }

    "count collection objects" in {
      val collectionObjectsCount = store.getCollectionObjectsCount(currentUserUri = currentUserUri, collectionUri = testData.collection.uri)
      collectionObjectsCount should equal(testData.objects.size)
    }

    "get an object by URI" in {
      val object_ = store.getObjectByUri(currentUserUri = currentUserUri, objectUri = testData.object_.uri)
      object_ should equal(testData.object_)
    }

    "list matching objects" in {
      val objects = store.getMatchingObjects(limit = 10, offset = 0, text = testData.object_.title, currentUserUri = currentUserUri).objects.map(object_ => object_.object_)
      // Will return all objects, but the exact match should be first
      objects(0) should equal(testData.object_)
      objects.sortBy(object_ => object_.uri.toString) should equal(testData.objects)
    }

//    "get matching object facets" in {
//      withUnknownHostExceptionCatch { () => {
//        val facets = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = currentUserUri).facets
//        facets.subjects.size should be > 1
//      }
//      }
//    }

    "get matching objects count" in {
      val count = store.getMatchingObjectsCount(text = testData.object_.title, currentUserUri = currentUserUri)
      // Will return all objects
      count should equal(10)
    }
  }
}
