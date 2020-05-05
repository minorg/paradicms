package org.paradicms.lib.base.rdf.properties

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model._

import scala.collection.JavaConverters._

trait ResourceProperties {
  val resource: Resource

  def uri = Uri.parse(resource.getURI)

  protected def addProperty[RdfNodeT <: RDFNode](property: Property, values: List[RdfNodeT]): Unit = {
    for (value <- values) {
      resource.addProperty(property, value)
    }
  }

  protected def addPropertyLiteral[T](property: Property, values: List[T]) = {
    addProperty(property, values.map(value => ResourceFactory.createPlainLiteral(value.toString)))
  }

  protected final def getPropertyObjects(property: Property): List[RDFNode] =
    resource.listProperties(property).asScala.toList.map(statement => statement.getObject)

  protected final def getPropertyObjects(property: Property, lang: String): List[RDFNode] =
    resource.listProperties(property, lang).asScala.toList.map(statement => statement.getObject)

  protected final def getPropertyObjectFloats(property: Property): List[Float] =
    getPropertyObjectLiterals(property).map(literal => literal.getFloat)

  protected final def getPropertyObjectInts(property: Property): List[Int] =
    getPropertyObjectLiterals(property).map(literal => literal.getInt)

  protected final def getPropertyObjectLiterals(property: Property): List[Literal] =
    getPropertyObjects(property).filter(object_ => object_.isLiteral).map(object_ => object_.asLiteral())

  protected final def getPropertyObjectLiterals(property: Property, lang: String): List[Literal] =
    getPropertyObjects(property, lang).filter(object_ => object_.isLiteral).map(object_ => object_.asLiteral())

  protected final def getPropertyObjectLongs(property: Property): List[Long] =
    getPropertyObjectLiterals(property).map(literal => literal.getLong)

  protected final def getPropertyObjectResources(property: Property): List[Resource] =
    getPropertyObjects(property).filter(node => node.isResource).map(node => node.asResource())

  protected final def getPropertyObjectStrings(property: Property): List[String] =
    getPropertyObjectLiterals(property).map(literal => literal.getString)

  protected final def getPropertyObjectStrings(property: Property, lang: String): List[String] =
    getPropertyObjectLiterals(property, lang).map(literal => literal.getString)

  protected final def getPropertyObjectResourceUris(property: Property): List[String] =
    getPropertyObjects(property).filter(object_ => object_.isURIResource).map(object_ => object_.asResource.getURI)

  protected def setProperty[RdfNodeT <: RDFNode](property: Property, values: List[RdfNodeT]): Unit = {
    resource.removeAll(property)
    for (value <- values) {
      resource.addProperty(property, value)
    }
  }

  protected def setPropertyLiteral[T](property: Property, values: List[T]) = {
    resource.removeAll(property)
    addPropertyLiteral(property, values)
  }
}
