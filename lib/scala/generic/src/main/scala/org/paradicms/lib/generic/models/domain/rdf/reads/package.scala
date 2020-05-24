package org.paradicms.lib.generic.models.domain.rdf

import io.lemonlabs.uri.{Uri, Url}
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.paradicms.lib.base.rdf.properties.{DcResourceProperties, FoafResourceProperties}
import org.paradicms.lib.base.rdf.{Rdf, Reads}
import org.paradicms.lib.generic.models.domain._
import org.paradicms.lib.generic.rdf.properties.VraResourceProperties
import org.paradicms.lib.generic.rdf.vocabularies.{CMS, CONTACT, EXIF}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

package object reads {
  implicit class ResourceWrapper(val resource: Resource)
    extends DcResourceProperties
      with FoafResourceProperties
      with VraResourceProperties {
    def height = getPropertyObjectInts(EXIF.height).headOption
    def maxHeight = getPropertyObjectInts(CMS.imageMaxHeight).headOption
    def maxWidth = getPropertyObjectInts(CMS.imageMaxWidth).headOption
    def sortNames = getPropertyObjectStrings(CONTACT.sortName)
    def width = getPropertyObjectInts(EXIF.width).headOption
  }

  implicit val collectionReads: Reads[Collection] = (resource: Resource) =>
    Collection(
      description = resource.descriptions.headOption,
      name = resource.titles.headOption.orElse(resource.names.headOption).get,
      rights = Rdf.read[Option[Rights]](resource.resource),
      uri = resource.uri
    )

  implicit val derivedImageSetReads: Reads[DerivedImageSet] = (resource: Resource) =>
    DerivedImageSet(
      original = Rdf.read[Image](resource.resource),
      derived = resource.thumbnails.map(resource => Rdf.read[Image](resource))
    )

  implicit val imageReads: Reads[Image] = (resource: Resource) => {
    val exactHeight = resource.height
    val exactWidth = resource.width
    val logger = LoggerFactory.getLogger(Image.getClass)
    val maxHeight = resource.maxHeight
    val maxWidth = resource.maxWidth
    val url = resource.uri.asInstanceOf[Url]

    Image(
      exactDimensions = if (exactHeight.isDefined || exactWidth.isDefined) {
        if (exactHeight.isDefined && exactWidth.isDefined) {
          Some(ImageDimensions(height = exactHeight.get, width = exactWidth.get))
        } else if (exactHeight.isDefined) {
          logger.warn("image {} has exact height defined but not exact width")
          None
        } else {
          logger.warn("image {} has exact width defined but not exact height")
          None
        }
      } else {
        None
      },
      maxDimensions = if (maxHeight.isDefined || maxWidth.isDefined) {
        if (maxHeight.isDefined && maxWidth.isDefined) {
          Some(ImageDimensions(height = maxHeight.get, width = maxWidth.get))
        } else if (maxHeight.isDefined) {
          logger.warn("image {} has max height defined but not max width")
          None
        } else {
          logger.warn("image {} has max width defined but not max height")
          None
        }
      } else {
        None
      },
      url = url
    )
  }

  implicit val institutionReads: Reads[Institution] = (resource: Resource) =>
    Institution(
      name = resource.names.head,
      rights = Rdf.read[Option[Rights]](resource.resource),
      uri = resource.uri
    )

  implicit val objectReads: Reads[Object] = (resource: Resource) => {
    val descriptions = resource.descriptions
    Object(
      alternativeTitles = resource.alternatives,
      creators = resource.creators,
      culturalContexts = resource.culturalContexts,
      dates = resource.dates,
      description = if (!descriptions.isEmpty) Some(descriptions(0)) else None,
      descriptions = descriptions,
      extents = resource.extents,
      identifiers = resource.identifiers,
      images = resource.resource.listProperties(FOAF.depiction).asScala.toList.map(statement => Rdf.read[DerivedImageSet](statement.getObject.asResource())),
      languages = resource.languages,
      materials = resource.materials,
      media = resource.media,
      provenances = resource.provenances,
      publishers = resource.publishers,
      rights = Rdf.read[Option[Rights]]((resource.resource)),
      sources = resource.sources,
      spatials = resource.spatials,
      subjects = resource.subjects,
      techniques = resource.hasTechniques,
      temporals = resource.temporals,
      title = (resource.titles ::: resource.alternatives)(0),
      titles = resource.titles,
      types = resource.types.filter(`type` => `type`.isLiteral).map(typeLiteral => typeLiteral.asLiteral().getString),
      uri = resource.uri
    )
  }

  implicit val personReads: Reads[Person] = (resource: Resource) =>
    Person(
      familyName=resource.familyNames.headOption,
      givenName=resource.givenNames.headOption,
      name=resource.names.headOption,
      sortName=resource.sortNames.headOption,
      uri=resource.uri
    )

  implicit val rightsReads: Reads[Option[Rights]] = (resource: Resource) => {
    val rights = resource.rights
    if (!rights.isEmpty) {
      val statementUris = rights.filter(node => node.isURIResource).map(node => Uri.parse(node.asResource().getURI))
      val texts = rights.filter(node => node.isLiteral).map(node => node.asLiteral().getString)
      if (!statementUris.isEmpty && !texts.isEmpty) {
        Some(Rights(
          holder = resource.rightsHolders.headOption,
          license = resource.licenses.headOption,
          statementUri = statementUris.headOption,
          text = if (!texts.isEmpty) Some(texts.mkString("\n")) else None
        ))
      } else {
        None
      }
    } else {
      None
    }
  }

  implicit val userReads: Reads[User] = (resource: Resource) =>
    User(
      email = resource.mboxes.map(uri => uri.path.toString()).headOption,
      name = resource.names.head,
      uri = resource.uri
    )
}
