package org.paradicms.lib.generic.models.domain.rdf

import org.apache.jena.rdf.model.{Model, ResourceFactory}
import org.apache.jena.sparql.vocabulary.FOAF
import org.paradicms.lib.base.rdf.{Rdf, Writes}
import org.paradicms.lib.generic.models.domain.{DerivedImageSet, Image}
import org.paradicms.lib.generic.rdf.vocabularies.{CMS, EXIF}

package object writes {
  implicit val imageWrites: Writes[Image] = (model: Model, image: Image) => {
    val resource = model.createResource(image.uri.toString)
    if (image.exactDimensions.isDefined) {
      resource.addProperty(EXIF.height, ResourceFactory.createTypedLiteral(image.exactDimensions.get.height))
      resource.addProperty(EXIF.width, ResourceFactory.createTypedLiteral(image.exactDimensions.get.width))
    }
    if (image.maxDimensions.isDefined) {
      resource.addProperty(CMS.imageMaxHeight, ResourceFactory.createTypedLiteral(image.maxDimensions.get.height))
      resource.addProperty(CMS.imageMaxWidth, ResourceFactory.createTypedLiteral(image.maxDimensions.get.width))
    }
    resource
  }

  implicit val derivedImageSetWrites: Writes[DerivedImageSet] = (model: Model, derivedImageSet: DerivedImageSet) => {
    val resource = Rdf.write(model, derivedImageSet.original)
    for (derivedImage <- derivedImageSet.derived) {
      resource.addProperty(FOAF.thumbnail, Rdf.write(model, derivedImage))
    }
    resource
  }
}
