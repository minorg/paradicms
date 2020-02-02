package org.paradicms.lib.generic.stores

import javax.inject.Inject
import play.api.Configuration

class GenericSparqlStore(val configuration: SparqlStoreConfiguration) extends GenericStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  @Inject
  def this(configuration: Configuration) = this(SparqlStoreConfiguration(configuration))
}
