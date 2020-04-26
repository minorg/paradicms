package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.FoafResourceProperties
import org.paradicms.lib.generic.rdf.vocabularies.CONTACT

final case class Person(
                         familyName: Option[String],
                         givenName: Option[String],
                         name: Option[String],
                         sortName: Option[String],
                         uri: Uri
                       )

object Person {
  implicit class PersonResource(val resource: Resource) extends FoafResourceProperties {
    def sortNames = getPropertyObjectStrings(CONTACT.sortName)
  }

  def apply(resource: PersonResource): Person =
    Person(
      familyName=resource.familyNames.headOption,
      givenName=resource.givenNames.headOption,
      name=resource.names.headOption,
      sortName=resource.sortNames.headOption,
      uri=resource.uri
    )
}