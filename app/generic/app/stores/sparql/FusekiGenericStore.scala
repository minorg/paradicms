package stores.sparql

import javax.inject.{Inject, Singleton}
import org.paradicms.lib.generic.stores.sparql._
import play.api.{Configuration, Logger}
import stores.GenericStore

@Singleton
class FusekiGenericStore(val configuration: FusekiSparqlStoreConfiguration) extends GenericStore with FusekiSparqlStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  protected val logger = Logger(classOf[FusekiGenericStore]).logger

  @Inject
  def this(configuration: Configuration) = this(FusekiSparqlStoreConfiguration(configuration))
}
