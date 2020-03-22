package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.{Collection, Institution}

final case class MatchingObjects(
                                  collectionsByUri: Map[Uri, Collection],
                                  facets: ObjectFacets,
                                  institutionsByUri: Map[Uri, Institution],
                                  objects: List[MatchingObject]
                                )
