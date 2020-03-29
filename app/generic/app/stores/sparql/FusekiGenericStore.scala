package stores.sparql

import javax.inject.{Inject, Singleton}
import org.paradicms.lib.generic.stores.sparql._
import play.api.Configuration
import stores.GenericStore

@Singleton
class FusekiGenericStore(val configuration: FusekiSparqlStoreConfiguration) extends GenericStore with FusekiSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  @Inject
  def this(configuration: Configuration) = this(FusekiSparqlStoreConfiguration(configuration))
}
