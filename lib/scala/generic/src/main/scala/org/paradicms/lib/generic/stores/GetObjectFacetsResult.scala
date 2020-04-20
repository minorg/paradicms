package org.paradicms.lib.generic.stores

import org.paradicms.lib.generic.models.domain.{Collection, Institution}

final case class GetObjectFacetsResult(
                                  collections: List[Collection],
                                  facets: ObjectFacets,
                                  institutions: List[Institution]
                                )
