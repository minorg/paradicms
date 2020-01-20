package models.graphql

import org.paradicms.service.lib.models.graphql.AbstractGraphQlSchemaContext
import org.paradicms.service.lib.stores.Store
import play.api.mvc.Request

class GenericGraphQlSchemaContext(request: Request[_], val store: Store) extends AbstractGraphQlSchemaContext(request, store)
