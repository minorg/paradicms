package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution, Object, User}

object TestData {
  val institution = Institution(uri = Uri.parse("http://example.com/institution"), name = "Test institution")
  val collection = Collection(uri = Uri.parse("http://example.com/collection"), name = "Test collection")
  val object_ = Object(uri = Uri.parse("http://example.com/object"), title = "Test object", titles = List("Test object"))
  val user = User(email = Some("test@example.com"), name = "Test user", uri = Uri.parse("http://example.com/user"))
}
