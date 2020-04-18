package org.paradicms.lib.generic.stores

final case class ObjectFilters(
                                collectionUris: Option[UriFacetFilter],
                                institutionUris: Option[UriFacetFilter],
                                subjects: Option[StringFacetFilter],
                                types: Option[StringFacetFilter]
                              )
