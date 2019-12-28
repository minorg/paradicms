package models.domain

import io.lemonlabs.uri.Url
import models.domain.vocabulary.EXIF

final case class Image(
                        height: Option[Int] = None,
                        url: Url,
                        width: Option[Int] = None
                      ) extends DomainModel {
  val uri = url
}

object Image extends DomainModelCompanion {
  def apply(resource: ResourceWrapper): Image = {
    Image(
      height = resource.getPropertyObjectInt(EXIF.height),
      url = resource.uri.asInstanceOf[Url],
      width = resource.getPropertyObjectInt(EXIF.width)
    )
  }
}
