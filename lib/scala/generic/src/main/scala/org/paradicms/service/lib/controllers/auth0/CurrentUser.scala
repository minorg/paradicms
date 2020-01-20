package org.paradicms.service.lib.controllers.auth0

import io.lemonlabs.uri.Uri
import org.paradicms.service.lib.models.domain.User
import org.paradicms.service.lib.stores.Store
import play.api.mvc.{Request, Session}

class CurrentUser(store: Store) {
  def get(request: Request[_]): Option[User] = {
    request.session.get("userUri").flatMap(userUri => store.getUserByUri(Uri.parse(userUri)))
  }

  def put(user: User): Session = {
    store.putUser(user)
    Session(Map("userUri" -> user.uri.toString()))
  }
}
