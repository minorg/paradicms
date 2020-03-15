package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.paradicms.lib.base.models.domain.DublinCoreResourceProperties

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
                         rights: Option[Rights] = None,
                         provenances: List[String] = List(),
                         publishers: List[String] = List(),
                         sources: List[String] = List(),
                         spatialCoverages: List[String] = List(),
                         subjects: List[String] = List(),
                         title: String,
                         titles: List[String],
                         uri: Uri
                       )

object Object {
  implicit class ObjectResource(val resource: Resource) extends DublinCoreResourceProperties

  def apply(resource: ObjectResource): Object = {
    val descriptions = resource.descriptions()
    Object(
      alternativeTitles = resource.alternativeTitles,
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
      spatialCoverages = resource.spatialCoverages,
      subjects = resource.subjects(),
      title = (resource.titles() ::: resource.alternativeTitles()) (0),
      titles = resource.titles,
      uri = resource.uri
    )
  }
}
