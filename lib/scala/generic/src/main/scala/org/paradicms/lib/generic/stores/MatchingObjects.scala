package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution}

final case class MatchingObjects(
                                  collections: List[Collection],
                                  facets: ObjectFacets,
                                  institutions: List[Institution],
                                  objects: List[MatchingObject]
                                )
