package stores.sparql

import javax.inject.Inject
import org.paradicms.lib.generic.stores.sparql.{FusekiSparqlStore, FusekiSparqlStoreConfiguration, SparqlCollectionStore, SparqlUserStore}
import play.api.Configuration
import stores.BookStore

final class FusekiBookStore @Inject()(systemConfiguration: Configuration)
  extends FusekiSparqlStore
    with BookStore
    with SparqlCollectionStore
    with SparqlUserStore {
  val configuration = FusekiSparqlStoreConfiguration(systemConfiguration)
}
