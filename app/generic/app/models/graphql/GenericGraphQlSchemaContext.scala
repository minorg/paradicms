package models.graphql

import org.paradicms.service.lib.generic.models.graphql.AbstractGraphQlSchemaContext
import org.paradicms.service.lib.generic.stores.Store
import play.api.mvc.Request

class GenericGraphQlSchemaContext(request: Request[_], val store: Store) extends AbstractGraphQlSchemaContext(request, store)
