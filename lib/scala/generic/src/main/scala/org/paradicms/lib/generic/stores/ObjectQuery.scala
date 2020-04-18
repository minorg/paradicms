package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

final case class ObjectQuery(
                               filters: Option[ObjectFilters],
                               text: Option[String]
                             )

object ObjectQuery {
  def collection(collectionUri: Uri) =
    new ObjectQuery(
      filters = Some(ObjectFilters(
        collectionUris = Some(UriFacetFilter(exclude = None, include = Some(List(collectionUri)))),
        institutionUris = None,
        subjects = None,
        types = None
      )),
      text = None
    )

  def institution(institutionUri: Uri) =
    new ObjectQuery(
      filters = Some(ObjectFilters(
        collectionUris = None,
        institutionUris = Some(UriFacetFilter(exclude = None, include = Some(List(institutionUri)))),
        subjects = None,
        types = None
      )),
      text = None
    )

  def text(text: String) =
    new ObjectQuery(
      filters = None,
      text = Some(text)
    )
}
