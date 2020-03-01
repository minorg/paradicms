package stores

import io.lemonlabs.uri.Url
import org.paradicms.lib.generic.stores.{GenericTestData, SparqlStoreConfiguration}
import org.scalatest.{Matchers, WordSpec}

// The SparqlStore is populated out-of-band. These tests are meant to be run on a populated store.
class GenericSparqlStoreSpec extends WordSpec with Matchers {
  "SPARQL store" should {
    val testData = new GenericTestData
    val currentUserUri = Option(testData.user.uri)
    val store = new GenericSparqlStore(SparqlStoreConfiguration(sparqlQueryUrl = Url.parse("http://fuseki:3030/ds/sparql"), sparqlUpdateUrl = Url.parse("http://fuseki:3030/ds/update")))

    "list all institutions" in {
      withUnknownHostExceptionCatch { () =>
        val institutions = store.getInstitutions(currentUserUri = currentUserUri)
        institutions.size should be > 0
      }
    }

    "get an institution by URI" in {
      withUnknownHostExceptionCatch { () =>
        val leftInstitution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val rightInstitution = store.getInstitutionByUri(currentUserUri = currentUserUri, institutionUri = leftInstitution.uri)
        leftInstitution should equal(rightInstitution)
      }
    }

    "list institution collections" in {
      withUnknownHostExceptionCatch { () =>
        val collections = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = store.getInstitutions(currentUserUri = currentUserUri)(0).uri)
        collections.size should be > 0
      }
    }

    "get collection by URI" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val leftCollection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
        val rightCollection = store.getCollectionByUri(currentUserUri = currentUserUri, collectionUri = leftCollection.uri)
        leftCollection should equal(rightCollection)
      }
    }

    "list collection objects" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = currentUserUri)(0)
        val collection = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)(0)
        val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 0)
        val objectWithImages = objects.find(object_ => !object_.images.isEmpty)
        objectWithImages should not be (null)
        val objectWithThumbnail = objects.find(object_ => object_.images.exists(image => image.thumbnail.isDefined))
        objectWithThumbnail should not be (null)
        objects.size should be(10)
        val nextObjects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 10, offset = 10)
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
        val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 1, offset = 0)
        objects.size should be(1)
        val expected = objects(0)
        val actual = store.getObjectByUri(currentUserUri = currentUserUri, objectUri = expected.uri)
        actual should equal(expected)
      }
    }

    "list matching objects" in {
      withUnknownHostExceptionCatch { () =>
        val objects = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = currentUserUri)
        objects.size should be > 1
      }
    }

    "get matching objects count" in {
      withUnknownHostExceptionCatch { () =>
        val count = store.getMatchingObjectsCount(text = "back", currentUserUri = currentUserUri)
        count should be > 1
      }
    }

    "put and get a user" in {
      withUnknownHostExceptionCatch { () =>
        store.putUser(testData.user)
        store.getUserByUri(testData.user.uri).get should equal(testData.user)
      }
    }
  }
}
