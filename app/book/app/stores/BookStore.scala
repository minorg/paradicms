package stores

import com.google.inject.ImplementedBy
import org.paradicms.lib.generic.stores.{CollectionStore, UserStore}

@ImplementedBy(classOf[BookSparqlStore])
trait BookStore extends CollectionStore with UserStore {
}
