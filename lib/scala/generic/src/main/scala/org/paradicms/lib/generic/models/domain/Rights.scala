package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.DcResourceProperties

final case class Rights(
                         holder: Option[String] = None,
                         license: Option[Uri] = None,
                         statementUri: Option[Uri] = None,
                         text: Option[String] = None
                       )

object Rights {
  implicit class RightsResource(val resource: Resource) extends DcResourceProperties

  def apply(resource: RightsResource): Option[Rights] = {
    val rights = resource.rights()
    if (rights.isEmpty) {
      return None
    }
    val statementUris = rights.filter(node => node.isURIResource).map(node => Uri.parse(node.asResource().getURI))
    val texts = rights.filter(node => node.isLiteral).map(node => node.asLiteral().getString)
    if (statementUris.isEmpty && texts.isEmpty) {
      return None
    }
    Some(Rights(
      holder = resource.rightsHolders.headOption,
      license = resource.licenses().headOption,
      statementUri = statementUris.headOption,
      text = if (!texts.isEmpty) Some(texts.mkString("\n")) else None
    ))
  }
}

