package org.paradicms.lib.base.rdf.properties

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF

trait FoafResourceProperties extends ResourceProperties {
  def familyNames: List[String] = getPropertyObjectStrings(FOAF.familyName)

  def givenNames: List[String] = getPropertyObjectStrings(FOAF.givenName)

  def mboxes: List[Uri] = getPropertyObjects(FOAF.mbox).flatMap(object_ => if (object_.isURIResource) Some(Uri.parse(object_.asResource().getURI)) else None)

  def names: List[String] = getPropertyObjectStrings(FOAF.name)

  def thumbnails: List[Resource] = getPropertyObjects(FOAF.thumbnail).flatMap(object_ => if (object_.isResource()) Some(object_.asResource()) else None)
}
