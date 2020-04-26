package org.paradicms.lib.generic.rdf.properties

import org.paradicms.lib.base.rdf.properties.ResourceProperties
import org.paradicms.lib.generic.rdf.vocabularies.VRA

trait VraResourceProperties extends ResourceProperties {
  def culturalContexts = getPropertyObjectStrings(VRA.culturalContext)

  def hasTechniques = getPropertyObjectStrings(VRA.hasTechnique)

  def materials = getPropertyObjectStrings(VRA.material)
}
