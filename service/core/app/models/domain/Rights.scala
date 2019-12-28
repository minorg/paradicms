package models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.vocabulary.DCTerms

final case class Rights(
                         holder: Option[String] = None,
                         license: Option[Uri] = None,
                         text: String
                       )

object Rights extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): Option[Rights] = {
    val texts = resource.dublinCore.rights()
    if (texts.isEmpty) {
      return None
    }
    Some(Rights(
      holder = resource.dublinCore.rightsHolder(),
      license = resource.getPropertyObject(DCTerms.license).flatMap(object_ => if (object_.isURIResource) Some(Uri.parse(object_.asResource().getURI)) else None),
      text = texts.mkString("\n"),
    ))
  }
}

