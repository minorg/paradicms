package models.graphql

import org.paradicms.lib.generic.stores.Store
import org.paradicms.service.lib.generic.models.graphql.AbstractGraphQlSchemaContext
import play.api.mvc.Request

class BookGraphQlSchemaContext(request: Request[_], val store: Store) extends AbstractGraphQlSchemaContext(request, store)
