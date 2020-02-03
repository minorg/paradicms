package org.paradicms.lib.generic.models.graphql

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.controllers.auth0.CurrentUser
import org.paradicms.lib.generic.models.domain.User
import org.paradicms.lib.generic.stores.UserStore
import play.api.mvc.Request

abstract class AbstractGraphQlSchemaContext(request: Request[_], userStore: UserStore) {
  private val currentUser_ = new CurrentUser(userStore)

  def currentUserUri(): Option[Uri] = currentUser().map(user => user.uri)

  def currentUser(): Option[User] = currentUser_.get(request)
}
