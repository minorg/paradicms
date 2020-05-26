package org.paradicms.lib.generic

import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object, User}
import org.paradicms.lib.generic.stores.ObjectQuery
import org.paradicms.lib.generic.stores.sparql._
import org.slf4j.LoggerFactory

object GenericTestData {
  private final class GenericTestSparqlStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
    val logger = LoggerFactory.getLogger(classOf[GenericTestSparqlStore])
  }
  private val store = new GenericTestSparqlStore
  private val currentUserUri = store.currentUserUri

  val institutions: List[Institution] = store.getInstitutions(currentUserUri = currentUserUri).sortBy(institution => institution.uri.toString())
  if (institutions.size != 1) throw new IllegalArgumentException
  val institution = institutions(0)
  val collections: List[Collection] = institutions.flatMap(institution => store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)).sortBy(collection => collection.uri.toString())
  private val institutionCollections = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)
  if (institutionCollections.size != 1) throw new IllegalArgumentException
  val collection = institutionCollections(0)
  val objects: List[Object] =
    collections.flatMap(collection => store.getObjects(currentUserUri = currentUserUri, limit = Int.MaxValue, offset = 0, query = ObjectQuery.collection(collection.uri)).objectsWithContext)
      .map(objectWithContext => objectWithContext.object_)
      .sortBy((object_ => object_.uri.toString()))
  val object_ = store.getObjects(currentUserUri = currentUserUri, limit = 1, offset = 0, query = ObjectQuery.collection(collection.uri)).objectsWithContext(0).object_
  val user = User(
    email = Some("test@example.com"),
    name = "Test user",
    uri = currentUserUri.get
  )
}
