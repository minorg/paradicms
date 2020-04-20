package org.paradicms.lib.generic.stores

import org.paradicms.lib.generic.models.domain.{Collection, Institution}

final case class GetObjectsResult(
                                  collections: List[Collection],
                                  institutions: List[Institution],
                                  objectsWithContext: List[ObjectWithContext]
                                )
