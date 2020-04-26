package org.paradicms.lib.base.rdf.properties

import io.lemonlabs.uri.Uri
import org.apache.jena.rdf.model.{Literal, Property, RDFNode, Resource}

import scala.collection.JavaConverters._

trait ResourceProperties {
  val resource: Resource

  def uri = Uri.parse(resource.getURI)

  protected def getPropertyObjectInts(property: Property): List[Int] =
    getPropertyObjectLiterals(property).map(literal => literal.getInt)

  protected def getPropertyObjectStrings(property: Property): List[String] =
    getPropertyObjectLiterals(property).map(literal => literal.getString)

  protected def getPropertyObjectLiterals(property: Property): List[Literal] =
    getPropertyObjects(property).flatMap(object_ => if (object_.isLiteral) Some(object_.asLiteral()) else None)

  protected def getPropertyObjects(property: Property): List[RDFNode] =
    resource.listProperties(property).asScala.toList.map(statement => statement.getObject)
}
