package org.paradicms.lib.generic.stores

final class SparqlUserStoreSpec extends AbstractSparqlStoreSpec {
  private final class TestSparqlUserStore(protected val configuration: SparqlStoreConfiguration) extends SparqlUserStore

  "SPARQL store" should {
    val store = new TestSparqlUserStore(configuration)

    "put and get a user" in {
      withUnknownHostExceptionCatch { () =>
        store.putUser(testData.user)
        store.getUserByUri(testData.user.uri).get should equal(testData.user)
      }
    }
  }
}
