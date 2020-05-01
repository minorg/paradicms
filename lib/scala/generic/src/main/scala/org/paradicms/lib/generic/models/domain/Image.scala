package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Url
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.rdf.properties.ResourceProperties
import org.paradicms.lib.generic.rdf.vocabularies.{CMS, EXIF}

final case class Image(
                        height: Option[Int] = None,
                        maxHeight: Option[Int] = None,
                        maxWidth: Option[Int] = None,
                        url: Url,
                        width: Option[Int] = None
                      ) {
  val uri = url
}

object Image {
  implicit class ImageResource(val resource: Resource) extends ResourceProperties {
    def height = getPropertyObjectInts(EXIF.height).headOption
    def maxHeight = getPropertyObjectInts(CMS.imageMaxHeight).headOption
    def maxWidth = getPropertyObjectInts(CMS.imageMaxWidth).headOption
    def width = getPropertyObjectInts(EXIF.width).headOption
  }

  def apply(resource: ImageResource): Image = {
    Image(
      height = resource.height,
      maxHeight = resource.maxHeight,
      maxWidth = resource.maxWidth,
      url = resource.uri.asInstanceOf[Url],
      width = resource.width
    )
  }
}
