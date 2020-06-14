package models.domain

import io.lemonlabs.uri.Uri
import models.domain.vocabulary.SCHEMA
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.DCTerms
import org.paradicms.lib.base.rdf.Rdf
import org.paradicms.lib.base.rdf.properties.DcResourceProperties
import org.paradicms.lib.generic.models.domain.Person
import org.paradicms.lib.generic.models.domain.rdf.reads._

final case class Book(
                     creators: List[Person] = List(),
                     description: Option[String] = None,
                     format: Option[String] = None,
                     isbn: Option[String] = None,
                     pageCount: Option[Int] = None,
                     publicationDate: Option[String] = None,
                     publisher: Option[String] = None,
                     subjects: List[String] = List(),
                     title: String,
                     uri: Uri
                   )

object Book {
  implicit class BookResource(val resource: Resource) extends DcResourceProperties {
    def creatorResources: List[Resource] = getPropertyObjects(DCTerms.creator).flatMap(object_ => if (object_.isURIResource) Some(object_.asResource()) else None)
    def pageCount: Option[Int] = getPropertyObjectInts(SCHEMA.pageCount).headOption
  }

  def apply(resource: BookResource): Book =
    Book(
      creators=resource.creatorResources.map(resource => Rdf.read[Person](resource)),
      description=resource.descriptions.headOption,
      format=resource.formats.headOption,
      isbn=resource.identifiers.headOption,
      pageCount=resource.pageCount,
      publicationDate=resource.dates.headOption,
      publisher=resource.publishers.headOption,
      subjects=resource.subjects,
      title=resource.titles.head,
      uri=resource.uri
    )
}
