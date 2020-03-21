package stores.sparql

import javax.inject.{Inject, Singleton}
import org.paradicms.lib.generic.stores.sparql._
import play.api.Configuration
import stores.GenericStore

@Singleton
class GenericSparqlStore(val configuration: SparqlStoreConfiguration) extends GenericStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  @Inject
  def this(configuration: Configuration) = this(SparqlStoreConfiguration(configuration))
}
