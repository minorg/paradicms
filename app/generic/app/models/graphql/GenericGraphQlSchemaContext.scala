package models.graphql

import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaContext
import play.api.mvc.Request
import stores.GenericStore

class GenericGraphQlSchemaContext(request: Request[_], val store: GenericStore) extends AbstractGraphQlSchemaContext(request, store)
