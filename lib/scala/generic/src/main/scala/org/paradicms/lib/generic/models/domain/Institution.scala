package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.FoafResourceProperties

final case class Institution(
                              name: String,
                              rights: Option[Rights] = None,
                              uri: Uri
                            )

object Institution {
  implicit class InstitutionResource(val resource: Resource) extends FoafResourceProperties

  def apply(resource: InstitutionResource): Institution =
    Institution(
      name = resource.names.head,
      rights = Rights(resource.resource),
      uri = resource.uri
    )
}
