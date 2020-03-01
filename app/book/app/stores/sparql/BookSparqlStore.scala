package stores.sparql

import javax.inject.Inject
import org.paradicms.lib.generic.stores.sparql.{SparqlCollectionStore, SparqlStoreConfiguration, SparqlUserStore}
import play.api.Configuration
import stores.BookStore

final class BookSparqlStore @Inject()(systemConfiguration: Configuration)
  extends BookStore
    with SparqlCollectionStore
    with SparqlUserStore {
  val configuration = SparqlStoreConfiguration(systemConfiguration)
}
