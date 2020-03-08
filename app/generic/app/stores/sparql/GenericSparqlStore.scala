package stores.sparql

import javax.inject.Inject
import org.paradicms.lib.generic.stores.sparql._
import play.api.Configuration
import stores.GenericStore

class GenericSparqlStore(val configuration: SparqlStoreConfiguration) extends GenericStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  @Inject
  def this(configuration: Configuration) = this(SparqlStoreConfiguration(configuration))
}
