package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.UnitSpec

final class SparqlObjectStoreSpec extends UnitSpec {

  private final class TestSparqlObjectStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore

  "SPARQL store" should {
    val store = new TestSparqlObjectStore
    val currentUserUri = store.currentUserUri

    "list collection objects" in {
      val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
      val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 0).objects
      val objectWithImages = objects.find(object_ => !object_.images.isEmpty)
      objectWithImages should not be (null)
      val objectWithThumbnail = objects.find(object_ => object_.images.exists(image => image.thumbnail.isDefined))
      objectWithThumbnail should not be (null)
      objects.size should be(10)
      val nextObjects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 10).objects
      nextObjects.size should be(10)
      nextObjects.map(object_ => object_.uri).toSet.intersect(objects.map(object_ => object_.uri).toSet).size should be(0)
    }

    "get collection object facets" in {
      val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
      val facets = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 0).facets
      facets.subjects.size should be > 0
    }

    "count collection objects" in {
      val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
      val collectionObjectsCount = store.getCollectionObjectsCount(currentUserUri = currentUserUri, collectionUri = collection.uri)
      collectionObjectsCount should be > 0
    }

    "get an object by URI" in {
      val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
      val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
      val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 1, offset = 0).objects
      objects.size should be(1)
      val expected = objects(0)
      val actual = store.getObjectByUri(currentUserUri = currentUserUri, objectUri = expected.uri)
      actual should equal(expected)
    }

    "list matching objects" in {
      val objects = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = currentUserUri).objects
      objects.size should be > 1
    }

//    "get matching object facets" in {
//      withUnknownHostExceptionCatch { () => {
//        val facets = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = currentUserUri).facets
//        facets.subjects.size should be > 1
//      }
//      }
//    }

    "get matching objects count" in {
      val count = store.getMatchingObjectsCount(text = "back", currentUserUri = currentUserUri)
      count should be > 1
    }
  }
}
