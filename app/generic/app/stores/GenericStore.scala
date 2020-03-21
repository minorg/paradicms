package stores

import org.paradicms.lib.generic.stores.{CollectionStore, InstitutionStore, ObjectStore, UserStore}

trait GenericStore extends CollectionStore with InstitutionStore with ObjectStore with UserStore {
}
