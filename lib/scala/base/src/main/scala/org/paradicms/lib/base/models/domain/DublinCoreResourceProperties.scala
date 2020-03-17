package org.paradicms.lib.base.models.domain

import io.lemonlabs.uri.Uri
import org.apache.jena.vocabulary.{DCTerms, DC_11}

trait DublinCoreResourceProperties extends ResourceProperties {
  def alternativeTitles(): List[String] = getPropertyObjectStrings(DCTerms.alternative)

  def creators(): List[String] = getPropertyObjectStrings(DCTerms.creator) ::: getPropertyObjectStrings(DC_11.creator)

  def dates(): List[String] = getPropertyObjectStrings(DCTerms.date) ::: getPropertyObjectStrings(DC_11.date)

  def descriptions(): List[String] = getPropertyObjectStrings(DCTerms.description) ::: getPropertyObjectStrings(DC_11.description)

  def extents(): List[String] = getPropertyObjectStrings(DCTerms.extent)

  def identifiers(): List[String] = getPropertyObjectStrings(DCTerms.identifier) ::: getPropertyObjectStrings(DC_11.identifier)

  def formats(): List[String] = getPropertyObjectStrings(DCTerms.format) ::: getPropertyObjectStrings(DC_11.format)

  def languages(): List[String] = getPropertyObjectStrings(DCTerms.language) ::: getPropertyObjectStrings(DC_11.language)

  def licenses(): List[Uri] = getPropertyObjects(DCTerms.license).flatMap(object_ => if (object_.isURIResource) Some(Uri.parse(object_.asResource().getURI)) else None)

  def media(): List[String] = getPropertyObjectStrings(DCTerms.medium)

  def provenances(): List[String] = getPropertyObjectStrings(DCTerms.provenance)

  def publishers(): List[String] = getPropertyObjectStrings(DCTerms.publisher) ::: getPropertyObjectStrings(DC_11.publisher)

  def rights(): List[String] =
    getPropertyObjectStrings(DCTerms.rights) ::: getPropertyObjectStrings(DC_11.rights)

  def rightsHolders(): List[String] = getPropertyObjectStrings(DCTerms.rightsHolder)

  def sources(): List[String] = getPropertyObjectStrings(DCTerms.source) ::: getPropertyObjectStrings(DC_11.source)

  def spatialCoverages(): List[String] = getPropertyObjectStrings(DCTerms.spatial)

  def subjects(): List[String] = getPropertyObjectStrings(DCTerms.subject)

  def titles(): List[String] = getPropertyObjectStrings(DCTerms.title) ::: getPropertyObjectStrings(DC_11.title)
}