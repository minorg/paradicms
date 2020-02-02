package stores

import com.google.inject.ImplementedBy
import org.paradicms.lib.generic.stores.GenericStore

@ImplementedBy(classOf[BookSparqlStore])
trait BookStore extends GenericStore {
}
