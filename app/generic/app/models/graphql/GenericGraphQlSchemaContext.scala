package models.graphql

import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaContext
import org.paradicms.lib.generic.stores.GenericStore
import play.api.mvc.Request

class GenericGraphQlSchemaContext(request: Request[_], val store: GenericStore) extends AbstractGraphQlSchemaContext(request, store)
