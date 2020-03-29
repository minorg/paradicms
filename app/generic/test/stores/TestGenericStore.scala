package stores

import org.paradicms.lib.generic.stores.sparql._

final class TestGenericStore extends TestSparqlStore with GenericStore with SparqlCollectionStore with SparqlInstitutionStore with SparqlObjectStore with SparqlUserStore
