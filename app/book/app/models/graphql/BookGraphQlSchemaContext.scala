package models.graphql

import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaContext
import org.paradicms.lib.generic.stores.Store
import play.api.mvc.Request

class BookGraphQlSchemaContext(request: Request[_], val store: Store) extends AbstractGraphQlSchemaContext(request, store)
