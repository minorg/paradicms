package org.paradicms.lib.generic.stores.test

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.User
import org.paradicms.lib.generic.stores.{GenericTestData, UserStore}

trait TestUserStore extends UserStore {
  protected val testData: GenericTestData

  override def getUserByUri(userUri: Uri): Option[User] = if (userUri == testData.user.uri) Some(testData.user) else None

  override def putUser(user: User): Unit = throw new UnsupportedOperationException
}
