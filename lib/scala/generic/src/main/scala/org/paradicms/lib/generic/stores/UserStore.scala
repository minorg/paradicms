package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.User

trait UserStore {
  def getUserByUri(userUri: Uri): Option[User]

  def putUser(user: User)
}
