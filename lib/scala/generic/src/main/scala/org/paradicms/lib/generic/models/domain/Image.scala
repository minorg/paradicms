package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Url
import org.apache.jena.rdf.model.Resource
import org.paradicms.lib.base.models.domain.ResourceProperties
import org.paradicms.lib.generic.models.domain.vocabulary.EXIF

final case class Image(
                        height: Option[Int] = None,
                        url: Url,
                        width: Option[Int] = None
                      ) {
  val uri = url
}

object Image {
  implicit class ImageResource(val resource: Resource) extends ResourceProperties {
    def height = getPropertyObjectInts(EXIF.height).headOption
    def width = getPropertyObjectInts(EXIF.width).headOption
  }

  def apply(resource: ImageResource): Image = {
    Image(
      height = resource.height,
      url = resource.uri.asInstanceOf[Url],
      width = resource.width
    )
  }
}
