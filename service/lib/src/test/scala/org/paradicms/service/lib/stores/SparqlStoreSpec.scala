package org.paradicms.service.lib.stores

import java.net.UnknownHostException

import io.lemonlabs.uri.Url
import org.apache.jena.atlas.web.HttpException
import org.apache.jena.query.QueryException
import org.scalatest.{Assertion, Matchers, WordSpec}

// The SparqlStore is populated out-of-band. These tests are meant to be run on a populated store.
class SparqlStoreSpec extends WordSpec with Matchers {
  "SPARQL store" should {
    val currentUserUri = Option(TestData.user.uri)
    val store = new SparqlStore(sparqlQueryUrl = Url.parse("http://fuseki:3030/ds/sparql"), sparqlUpdateUrl = Url.parse("http://fuseki:3030/ds/update"))

    def withUnknownHostExceptionCatch(test: () => Assertion): Assertion =
      try {
        test()
      } catch {
        case e: HttpException => e.getCause match {
          case _: UnknownHostException => assert(true)
          case _ => throw e
        }
        case e: QueryException => e.getCause match {
          case _: UnknownHostException => assert(true)
          case _ => throw e
        }
      }

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
        store.putUser(TestData.user)
        store.getUserByUri(TestData.user.uri).get should equal(TestData.user)
      }
    }
  }
}
