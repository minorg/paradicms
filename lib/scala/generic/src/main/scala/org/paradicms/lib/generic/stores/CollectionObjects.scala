package org.paradicms.lib.generic.stores

import org.paradicms.lib.generic.models.domain.Object

final case class CollectionObjects(
                                    facets: ObjectFacets,
                                    objects: List[Object]
                                  )
