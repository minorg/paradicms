package stores

import io.lemonlabs.uri.Uri
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.paradicms.lib.generic.stores.sparql._
import org.paradicms.lib.test.stores.sparql.DatasetSparqlStore
import org.slf4j.LoggerFactory

final class TestGenericStore extends DatasetSparqlStore with GenericStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore {
  def reset(): Unit = {
    this.dataset.getDefaultModel.removeAll()
    RDFDataMgr.read(dataset, getClass.getResourceAsStream("/generic_test_data.ttl"), Lang.TURTLE)
  }
  reset()

  val currentUserUri = Some(Uri.parse("http://example.com/user"))
  val logger = LoggerFactory.getLogger(classOf[TestGenericStore])
}
