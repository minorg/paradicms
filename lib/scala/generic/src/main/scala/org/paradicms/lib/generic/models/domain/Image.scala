package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Url

final case class Image(
                        exactDimensions: Option[ImageDimensions],
                        maxDimensions: Option[ImageDimensions],
                        url: Url
                      ) {
  val uri = url
}
