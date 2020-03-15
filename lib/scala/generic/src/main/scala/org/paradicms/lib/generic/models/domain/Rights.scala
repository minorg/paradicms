package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.models.domain.DublinCoreResourceProperties

final case class Rights(
                         holder: Option[String] = None,
                         license: Option[Uri] = None,
                         text: String
                       )

object Rights {
  implicit class RightsResource(val resource: Resource) extends DublinCoreResourceProperties

  def apply(resource: RightsResource): Option[Rights] = {
    val texts = resource.rights()
    if (texts.isEmpty) {
      return None
    }
    Some(Rights(
      holder = resource.rightsHolders.headOption,
      license = resource.licenses().headOption,
      text = texts.mkString("\n"),
    ))
  }
}

