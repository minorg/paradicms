package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.User

trait TestUserStore extends UserStore {
  protected val testData: GenericTestData

  override def getUserByUri(userUri: Uri): Option[User] = if (userUri == testData.user.uri) Some(testData.user) else None

  override def putUser(user: User): Unit = throw new UnsupportedOperationException
}
