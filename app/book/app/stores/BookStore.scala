package stores

import com.google.inject.ImplementedBy
import org.paradicms.lib.generic.stores.{CollectionStore, UserStore}
import stores.sparql.FusekiBookStore

@ImplementedBy(classOf[FusekiBookStore])
trait BookStore extends CollectionStore with UserStore {
}
