package org.paradicms.lib.generic.models.graphql

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.controllers.auth0.CurrentUser
import org.paradicms.lib.generic.models.domain.User
import org.paradicms.lib.generic.stores.Store
import play.api.mvc.Request

abstract class AbstractGraphQlSchemaContext(request: Request[_], store: Store) {
  private val currentUser_ = new CurrentUser(store)

  def currentUser(): Option[User] = currentUser_.get(request)

  def currentUserUri(): Option[Uri] = currentUser().map(user => user.uri)
}
