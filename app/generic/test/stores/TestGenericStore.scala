package stores

import org.paradicms.lib.generic.stores._

final class TestGenericStore extends GenericStore with TestCollectionStore with TestInstitutionStore with TestObjectStore with TestUserStore {
  protected val testData = new GenericTestData
}
