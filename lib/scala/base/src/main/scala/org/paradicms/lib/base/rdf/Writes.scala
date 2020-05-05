package org.paradicms.lib.base.rdf

import org.apache.jena.rdf.model.{Model, Resource}

trait Writes[A] {
  def write(model: Model, value: A): Resource
}
