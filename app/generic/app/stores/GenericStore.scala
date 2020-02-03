package stores

import com.google.inject.ImplementedBy
import org.paradicms.lib.generic.stores.{CollectionStore, InstitutionStore, ObjectStore, UserStore}

@ImplementedBy(classOf[GenericSparqlStore])
trait GenericStore extends CollectionStore with InstitutionStore with ObjectStore with UserStore {
}
