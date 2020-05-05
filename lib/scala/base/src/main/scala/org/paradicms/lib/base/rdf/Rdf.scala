package org.paradicms.lib.base.rdf

import org.apache.jena.rdf.model.{Model, Resource}

object Rdf {
  def read[A](resource: Resource)(implicit r: Reads[A]): A =
    r.read(resource)

  def write[A](model: Model, value: A)(implicit w: Writes[A]): Resource =
    w.write(model, value)
}