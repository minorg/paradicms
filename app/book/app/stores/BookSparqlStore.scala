package stores

import javax.inject.Inject
import org.paradicms.lib.generic.stores.GenericSparqlStore
import play.api.Configuration

class BookSparqlStore @Inject() (configuration: Configuration) extends GenericSparqlStore(configuration) with BookStore {
}
