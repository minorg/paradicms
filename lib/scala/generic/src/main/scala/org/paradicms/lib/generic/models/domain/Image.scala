package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Url
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.ResourceProperties
import org.paradicms.lib.generic.rdf.vocabularies.{CMS, EXIF}
import org.slf4j.LoggerFactory

final case class Image(
                        exactDimensions: Option[ImageDimensions] = None,
                        maxDimensions: Option[ImageDimensions] = None,
                        url: Url
                      ) {
  val uri = url
}

object Image {
  private val logger = LoggerFactory.getLogger(Image.getClass)

  implicit class ImageResource(val resource: Resource) extends ResourceProperties {
    def height = getPropertyObjectInts(EXIF.height).headOption
    def maxHeight = getPropertyObjectInts(CMS.imageMaxHeight).headOption
    def maxWidth = getPropertyObjectInts(CMS.imageMaxWidth).headOption
    def width = getPropertyObjectInts(EXIF.width).headOption
  }

  def apply(resource: ImageResource): Image = {
    val exactHeight = resource.height
    val exactWidth = resource.width
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
}
