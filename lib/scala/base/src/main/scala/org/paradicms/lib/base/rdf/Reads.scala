package org.paradicms.lib.base.rdf

import org.apache.jena.rdf.model.Resource

trait Reads[A] {
  def read(resource: Resource): A
}
