package org.paradicms.lib.generic

import org.paradicms.lib.generic.stores.sparql._

object GenericTestData {
  private final class GenericTestSparqlStore extends TestSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore
  private val store = new GenericTestSparqlStore
  private val currentUserUri = store.currentUserUri

  val institutions = store.getInstitutions(currentUserUri = currentUserUri)
  val institution = institutions(0)
  val collections = institutions.flatMap(institution => store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri))
  private val institutionCollections = store.getInstitutionCollections(currentUserUri = currentUserUri, institutionUri = institution.uri)
  if (institutionCollections.size != 1) throw new IllegalArgumentException
  val collection = institutionCollections(0)
  val objects = collections.flatMap(collection => store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = Int.MaxValue, offset = 0).objects)
  val object_ = store.getCollectionObjects(collectionUri = collection.uri, currentUserUri = currentUserUri, limit = 1, offset = 0).objects(0)
  val user = store.getUserByUri(currentUserUri.get).get
}
