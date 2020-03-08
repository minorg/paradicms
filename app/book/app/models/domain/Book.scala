package models.domain

import io.lemonlabs.uri.Uri
import models.domain.vocabulary.SCHEMA
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.DCTerms
import org.paradicms.lib.generic.models.Person
import org.paradicms.lib.generic.models.domain.{DomainModel, DomainModelCompanion}

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
                   ) extends DomainModel

object Book extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): Book =
    Book(
      creators=resource.getPropertyObjects(DCTerms.creator).flatMap(object_ => if (object_.isURIResource) Some(Person(object_.asInstanceOf[Resource])) else None),
      description=resource.dublinCore.descriptions().headOption,
      format=resource.dublinCore.formats().headOption,
      isbn=resource.dublinCore.identifiers().headOption,
      pageCount=resource.getPropertyObjectLiteral(SCHEMA.pageCount).map(literal => literal.getInt),
      publicationDate=resource.dublinCore.dates().headOption,
      publisher=resource.dublinCore.publishers().headOption,
      subjects=resource.dublinCore.subjects(),
      title=resource.dublinCore.titles().head,
      uri=resource.uri
    )
}
