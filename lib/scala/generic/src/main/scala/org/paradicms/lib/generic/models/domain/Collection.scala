package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.models.domain.{DublinCoreResourceProperties, FoafResourceProperties}

final case class Collection(
                             description: Option[String] = None,
                             name: String,
                             rights: Option[Rights] = None,
                             uri: Uri
                           )

object Collection {
  implicit class CollectionResource(val resource: Resource)
    extends DublinCoreResourceProperties
  with FoafResourceProperties

  def apply(resource: CollectionResource): Collection =
    Collection(
      description = resource.descriptions.headOption,
      name = resource.titles.headOption.orElse(resource.names.headOption).get,
      rights = Rights(resource.resource),
      uri = resource.uri
    )
}
