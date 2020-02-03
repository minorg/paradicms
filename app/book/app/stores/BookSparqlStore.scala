package stores

import javax.inject.Inject
import org.paradicms.lib.generic.stores.{SparqlCollectionStore, SparqlStoreConfiguration, SparqlUserStore}
import play.api.Configuration

class BookSparqlStore @Inject()(systemConfiguration: Configuration)
  extends BookStore
    with SparqlCollectionStore
    with SparqlUserStore {
  val configuration = SparqlStoreConfiguration(systemConfiguration)
}
