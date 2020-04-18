package org.paradicms.lib.generic.stores.sparql

import org.paradicms.lib.generic.stores.{ObjectFilters, ObjectQuery, StringFacetFilter}
import org.paradicms.lib.generic.{GenericTestData, UnitSpec}

final class SparqlObjectStoreSpec extends UnitSpec {

  private final class TestSparqlObjectStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore

  "SPARQL store" should {
    val store = new TestSparqlObjectStore
    val currentUserUri = store.currentUserUri
    val testData = GenericTestData

    "list collection objects" in {
      val objects =
        store.getObjects(currentUserUri = currentUserUri, limit = 10, offset = 0, query = ObjectQuery.collection(testData.collection.uri))
          .objectsWithContext.map(objectWithContext => objectWithContext.object_)
          .sortBy(object_ => object_.uri.toString())
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
      val facets = store.getObjects(currentUserUri = currentUserUri, limit = 10, offset = 0, query = ObjectQuery.collection(testData.collection.uri)).facets
      val actualSubjects = facets.subjects.toList.sortBy(subject => subject)
      val expectedSubjects = testData.objects.flatMap(object_ => object_.subjects).toSet.toList.sorted
      actualSubjects should equal(expectedSubjects)
    }

    "exclude the subject of an object" in {
      val allSubjects = testData.objects.flatMap(object_ => object_.subjects).toSet.toList.sorted
      val objects = store.getObjects(currentUserUri = currentUserUri, limit = 10, offset = 0, query = ObjectQuery(
        filters = Some(ObjectFilters(
          collectionUris = None,
          institutionUris = None,
          subjects = Some(StringFacetFilter(include = None, exclude = Some(List(allSubjects(0))))),
          types = None
        )),
        text = None
      )).objectsWithContext
      objects.size should equal(9)
    }

    "include the subject of an object" in {
      val allSubjects = testData.objects.flatMap(object_ => object_.subjects).toSet.toList.sorted
      val objects = store.getObjects(currentUserUri = currentUserUri, limit = 10, offset = 0, query = ObjectQuery(
        filters = Some(ObjectFilters(
          collectionUris = None,
          institutionUris = None,
          subjects = Some(StringFacetFilter(exclude = None, include = Some(List(allSubjects(0))))),
          types = None
        )),
        text = None
      )).objectsWithContext
      objects.size should equal(1)
    }

    "count collection objects" in {
      val collectionObjectsCount = store.getObjectsCount(currentUserUri = currentUserUri, query = ObjectQuery.collection(testData.collection.uri))
      collectionObjectsCount should equal(testData.objects.size)
    }

    "get an object by URI" in {
      val object_ = store.getObjectByUri(currentUserUri = currentUserUri, objectUri = testData.object_.uri)
      object_ should equal(testData.object_)
    }

    "list institution objects" in {
      val objects =
        store.getObjects(currentUserUri = currentUserUri, limit = 10, offset = 0, query = ObjectQuery.institution(testData.institution.uri))
          .objectsWithContext.map(objectWithContext => objectWithContext.object_)
          .sortBy(object_ => object_.uri.toString())
      objects should equal(testData.objects)
    }

    "list matching objects" in {
      val objects = store.getObjects(limit = 10, offset = 0, query = ObjectQuery.text(testData.object_.title), currentUserUri = currentUserUri)
        .objectsWithContext.map(objectWithContext => objectWithContext.object_)
      // Will return all objects, but the exact match should be first
//      objects(0) should equal(testData.object_)
      objects.sortBy(object_ => object_.uri.toString) should equal(testData.objects)
    }

    "get matching objects count" in {
      val count = store.getObjectsCount(query = ObjectQuery.text(testData.object_.title), currentUserUri = currentUserUri)
      // Will return all objects
      count should equal(10)
    }
  }
}
