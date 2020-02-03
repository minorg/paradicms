package models.graphql

import org.paradicms.lib.generic.models.graphql.AbstractGraphQlSchemaContext
import play.api.mvc.Request
import stores.BookStore

class BookGraphQlSchemaContext(request: Request[_], val store: BookStore) extends AbstractGraphQlSchemaContext(request, store)
