package org.paradicms.lib.generic.stores.sparql

final class SparqlObjectStoreSpec extends AbstractSparqlStoreSpec {

  private final class TestSparqlObjectStore(protected val configuration: SparqlStoreConfiguration) extends SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore

  "SPARQL store" should {
    val store = new TestSparqlObjectStore(configuration)

    "list collection objects" in {
      withUnknownHostExceptionCatch { () =>
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
    }

    "count collection objects" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
        val collectionObjectsCount = store.getCollectionObjectsCount(currentUserUri = currentUserUri, collectionUri = collection.uri)
        collectionObjectsCount should be > 0
      }
    }

    "get an object by URI" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
        val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 1, offset = 0).objects
        objects.size should be(1)
        val expected = objects(0)
        val actual = store.getObjectByUri(currentUserUri = currentUserUri, objectUri = expected.uri)
        actual should equal(expected)
      }
    }

    "list matching objects" in {
      withUnknownHostExceptionCatch { () =>
        val objects = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = currentUserUri).objects
        objects.size should be > 1
      }
    }

    "get matching object facets" in {
      withUnknownHostExceptionCatch { () => {
        val facets = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = currentUserUri).facets
        facets.subjects.size should be > 1
      }
      }
    }

    "get matching objects count" in {
      withUnknownHostExceptionCatch { () =>
        val count = store.getMatchingObjectsCount(text = "back", currentUserUri = currentUserUri)
        count should be > 1
      }
    }
  }
}
