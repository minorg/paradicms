package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.paradicms.lib.base.models.domain.DcResourceProperties

import scala.collection.JavaConverters._

final case class Object(
                         alternativeTitles: List[String] = List(),
                         creators: List[String] = List(),
                         dates: List[String] = List(),
                         description: Option[String] = None,
                         descriptions: List[String] = List(),
                         extents: List[String] = List(),
                         identifiers: List[String] = List(),
                         images: List[DerivedImageSet] = List(),
                         languages: List[String] = List(),
                         media: List[String] = List(),
                         provenances: List[String] = List(),
                         publishers: List[String] = List(),
                         rights: Option[Rights] = None,
                         sources: List[String] = List(),
                         spatials: List[String] = List(),
                         subjects: List[String] = List(),
                         temporals: List[String] = List(),
                         title: String,
                         titles: List[String],
                         types: List[String],
                         uri: Uri
                       )

object Object {
  implicit class ObjectResource(val resource: Resource) extends DcResourceProperties

  def apply(resource: ObjectResource): Object = {
    val descriptions = resource.descriptions()
    Object(
      alternativeTitles = resource.alternatives,
      creators = resource.creators,
      dates = resource.dates,
      description = if (!descriptions.isEmpty) Some(descriptions(0)) else None,
      descriptions = descriptions,
      extents = resource.extents,
      identifiers = resource.identifiers(),
      images = resource.resource.listProperties(FOAF.depiction).asScala.toList.map(statement => DerivedImageSet(statement.getObject.asResource())),
      languages = resource.languages,
      media = resource.media,
      provenances = resource.provenances,
      publishers = resource.publishers,
      rights = Rights(resource.resource),
      sources = resource.sources(),
      spatials = resource.spatials,
      subjects = resource.subjects(),
      temporals = resource.temporals(),
      title = (resource.titles() ::: resource.alternatives()) (0),
      titles = resource.titles,
      types = resource.types.filter(`type` => `type`.isLiteral).map(typeLiteral => typeLiteral.asLiteral().getString),
      uri = resource.uri
    )
  }
}
