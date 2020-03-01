package stores

import org.paradicms.lib.generic.stores._
import org.paradicms.lib.generic.stores.test.{TestCollectionStore, TestInstitutionStore, TestObjectStore, TestUserStore}

final class TestGenericStore extends GenericStore with TestCollectionStore with TestInstitutionStore with TestObjectStore with TestUserStore {
  protected val testData = new GenericTestData
}
