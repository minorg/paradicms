package org.paradicms.service.lib.stores

import java.net.UnknownHostException

import io.lemonlabs.uri.Url
import org.apache.jena.query.QueryException
import org.scalatest.{Assertion, Matchers, WordSpec}

// The SparqlStore is populated out-of-band. These tests are meant to be run on a populated store.
class SparqlStoreSpec extends WordSpec with Matchers {
  "SPARQL store" should {
    val store = new SparqlStore(Url.parse("http://fuseki:3030/ds/sparql"))

    def withUnknownHostExceptionCatch(test: () => Assertion): Assertion =
      try {
        test()
      } catch {
        case e: QueryException => e.getCause match {
          case _: UnknownHostException => assert(true)
          case _ => throw e
        }
      }

    "list all institutions" in {
      withUnknownHostExceptionCatch { () =>
        val institutions = store.getInstitutions(currentUserUri = None)
        institutions.size should be > 0
      }
    }

    "return an institution by URI" in {
      withUnknownHostExceptionCatch { () =>
        val leftInstitution = store.getInstitutions(currentUserUri = None)(0)
        val rightInstitution = store.getInstitutionByUri(currentUserUri = None, institutionUri = leftInstitution.uri)
        leftInstitution should equal(rightInstitution)
      }
    }

    "list institution collections" in {
      withUnknownHostExceptionCatch { () =>
        val collections = store.getInstitutionCollections(currentUserUri = None, institutionUri = store.getInstitutions(currentUserUri = None)(0).uri)
        collections.size should be > 0
      }
    }

    "return collection by URI" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = None)(0)
        val leftCollection = store.getInstitutionCollections(currentUserUri = None, institutionUri = institution.uri)(0)
        val rightCollection = store.getCollectionByUri(currentUserUri = None, collectionUri = leftCollection.uri)
        leftCollection should equal(rightCollection)
      }
    }

    "list collection objects" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = None)(0)
        val collection = store.getInstitutionCollections(currentUserUri = None, institutionUri = institution.uri)(0)
        val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = None, limit = 10, offset = 0)
        val objectWithImages = objects.find(object_ => !object_.images.isEmpty)
        objectWithImages should not be (null)
        val objectWithThumbnail = objects.find(object_ => object_.images.exists(image => image.thumbnail.isDefined))
        objectWithThumbnail should not be (null)
        objects.size should be(10)
        val nextObjects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = None, limit = 10, offset = 10)
        nextObjects.size should be(10)
        nextObjects.map(object_ => object_.uri).toSet.intersect(objects.map(object_ => object_.uri).toSet).size should be(0)
      }
    }

    "count collection objects" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = None)(0)
        val collection = store.getInstitutionCollections(currentUserUri = None, institutionUri = institution.uri)(0)
        store.getCollectionObjectsCount(currentUserUri = None, collectionUri = collection.uri) should be > 0
      }
    }

    "get an object by URI" in {
      withUnknownHostExceptionCatch { () =>
        val institution = store.getInstitutions(currentUserUri = None)(0)
        val collection = store.getInstitutionCollections(currentUserUri = None, institutionUri = institution.uri)(0)
        val objects = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = None, limit = 1, offset = 0)
        objects.size should be(1)
        val expected = objects(0)
        val actual = store.getObjectByUri(currentUserUri = None, objectUri = expected.uri)
        actual should equal(expected)
      }
    }

    "matching objects" in {
      withUnknownHostExceptionCatch { () =>
        val objects = store.getMatchingObjects(limit = 10, offset = 0, text = "back", currentUserUri = None)
        objects.size should be > 1
      }
    }

    "matching objects count" in {
      withUnknownHostExceptionCatch { () =>
        val count = store.getMatchingObjectsCount(text = "back", currentUserUri = None)
        count should be > 1
      }
    }
  }
}
