package org.paradicms.lib.generic.models.domain

final case class ImageDimensions(height: Int, width: Int) {
  def contains(other: ImageDimensions) =
    height >= other.height && width >= other.width
}
