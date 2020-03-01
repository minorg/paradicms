package stores

import org.paradicms.lib.test.stores._

final class TestGenericStore extends GenericStore with TestCollectionStore with TestInstitutionStore with TestObjectStore with TestUserStore {
  protected val testData = new GenericTestData
}
