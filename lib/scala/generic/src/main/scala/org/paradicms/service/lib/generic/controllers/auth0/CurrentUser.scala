package org.paradicms.service.lib.generic.controllers.auth0

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.stores.Store
import org.paradicms.service.lib.generic.models.domain.User
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
