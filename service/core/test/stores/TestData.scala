package stores

import io.lemonlabs.uri.Uri
import models.domain.{Collection, Institution, Object}

object TestData {
  val institution = Institution(uri = Uri.parse("http://example.com/institution"), name = "Test institution")
  val collection = Collection(uri = Uri.parse("http://example.com/collection"), name = "Test collection")
  val object_ = Object(uri = Uri.parse("http://example.com/object"), title = "Test object", titles = List("Test object"))
}
