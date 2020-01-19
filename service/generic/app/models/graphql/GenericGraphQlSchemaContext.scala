package models.graphql

import io.lemonlabs.uri.Uri
import org.paradicms.service.lib.controllers.auth0.CurrentUser
import org.paradicms.service.lib.models.domain.User
import org.paradicms.service.lib.stores.Store
import play.api.mvc.Request

class GenericGraphQlSchemaContext(request: Request[_], val store: Store) {
  private val currentUser_ = new CurrentUser(store)

  def currentUser(): Option[User] = currentUser_.get(request)

  def currentUserUri(): Option[Uri] = currentUser().map(user => user.uri)
}
