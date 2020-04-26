package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.FoafResourceProperties

final case class User(email: Option[String], name: String, uri: Uri)

object User {
  implicit class UserResource(val resource: Resource) extends FoafResourceProperties

  def apply(resource: UserResource): User =
    User(
      email = resource.mboxes.map(uri => uri.path.toString()).headOption,
      name = resource.names.head,
      uri = resource.uri
    )
}

