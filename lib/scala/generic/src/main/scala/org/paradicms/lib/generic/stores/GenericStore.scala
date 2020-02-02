package org.paradicms.lib.generic.stores

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[GenericSparqlStore])
trait GenericStore extends CollectionStore with InstitutionStore with ObjectStore with UserStore {
}
