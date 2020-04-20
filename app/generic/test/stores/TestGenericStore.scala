package stores

import org.paradicms.lib.generic.stores.sparql._
import org.slf4j.LoggerFactory

final class TestGenericStore extends TestSparqlStore with GenericStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  val logger = LoggerFactory.getLogger(classOf[TestGenericStore])
}
