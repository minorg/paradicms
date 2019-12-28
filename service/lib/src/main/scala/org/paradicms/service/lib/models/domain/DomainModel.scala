package models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.{Literal, Property, RDFNode, Resource}
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.{DCTerms, DC_11}

import scala.collection.JavaConverters._

trait DomainModel {
  val uri: Uri
}

trait DomainModelCompanion {

  implicit class ResourceWrapper(val resource: Resource) {

    object dublinCore {
      def alternativeTitles(): List[String] = getPropertyObjectStrings(DCTerms.alternative)

      def creators(): List[String] = getPropertyObjectStrings(DCTerms.creator) ::: getPropertyObjectStrings(DC_11.creator)

      def dates(): List[String] = getPropertyObjectStrings(DCTerms.date) ::: getPropertyObjectStrings(DC_11.date)

      def descriptions(): List[String] = getPropertyObjectStrings(DCTerms.description) ::: getPropertyObjectStrings(DC_11.description)

      def extents(): List[String] = getPropertyObjectStrings(DCTerms.extent)

      def identifiers(): List[String] = getPropertyObjectStrings(DCTerms.identifier) ::: getPropertyObjectStrings(DC_11.identifier)

      def languages(): List[String] = getPropertyObjectStrings(DCTerms.language) ::: getPropertyObjectStrings(DC_11.language)

      def media(): List[String] = getPropertyObjectStrings(DCTerms.medium)

      def provenances(): List[String] = getPropertyObjectStrings(DCTerms.provenance)

      def publishers(): List[String] = getPropertyObjectStrings(DCTerms.publisher) ::: getPropertyObjectStrings(DC_11.publisher)

      def rights(): List[String] =
        getPropertyObjectStrings(DCTerms.rights) ::: getPropertyObjectStrings(DC_11.rights)

      def rightsHolder(): Option[String] = getPropertyObjectString(DCTerms.rightsHolder)

      def sources(): List[String] = getPropertyObjectStrings(DCTerms.source) ::: getPropertyObjectStrings(DC_11.source)

      def spatialCoverages(): List[String] = getPropertyObjectStrings(DCTerms.spatial)

      def subjects(): List[String] = getPropertyObjectStrings(DCTerms.subject)

      def titles(): List[String] = getPropertyObjectStrings(DCTerms.title) ::: getPropertyObjectStrings(DC_11.title)
    }

    object foaf {
      def name(): Option[String] = getPropertyObjectString(FOAF.name)
    }

    def getPropertyObject(property: Property): Option[RDFNode] =
      Option(resource.getProperty(property)).map(statement => statement.getObject)

    def getPropertyObjects(property: Property): List[RDFNode] =
      resource.listProperties(property).asScala.toList.map(statement => statement.getObject)

    def getPropertyObjectInt(property: Property): Option[Int] =
      getPropertyObjectLiteral(property).map(literal => literal.getInt)

    def getPropertyObjectLiteral(property: Property): Option[Literal] =
      getPropertyObject(property).flatMap(object_ => if (object_.isLiteral) Some(object_.asLiteral()) else None)

    def getPropertyObjectLiterals(property: Property): List[Literal] =
      getPropertyObjects(property).flatMap(object_ => if (object_.isLiteral) Some(object_.asLiteral()) else None)

    def getPropertyObjectString(property: Property): Option[String] =
      getPropertyObjectLiteral(property).map(literal => literal.getString)

    def getPropertyObjectStrings(property: Property): List[String] =
      getPropertyObjectLiterals(property).map(literal => literal.getString)

    def uri: Uri = Uri.parse(resource.getURI)
  }

}