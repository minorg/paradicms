package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

final case class ObjectFilters(
                                collectionUris: Option[UriFacetFilter],
                                institutionUris: Option[UriFacetFilter],
                                subjects: Option[StringFacetFilter],
                                types: Option[StringFacetFilter]
                              )

object ObjectFilters {
  def collection(collectionUri: Uri) =
    ObjectFilters(
      collectionUris = Some(UriFacetFilter(exclude = None, include = Some(List(collectionUri)))),
      institutionUris = None,
      subjects = None,
      types = None
    )

  def institution(institutionUri: Uri) =
    ObjectFilters(
      collectionUris = None,
      institutionUris = Some(UriFacetFilter(exclude = None, include = Some(List(institutionUri)))),
      subjects = None,
      types = None
    )
}
