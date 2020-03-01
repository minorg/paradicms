package stores

import com.google.inject.ImplementedBy
import org.paradicms.lib.generic.stores.{CollectionStore, UserStore}
import stores.sparql.BookSparqlStore

@ImplementedBy(classOf[BookSparqlStore])
trait BookStore extends CollectionStore with UserStore {
}
