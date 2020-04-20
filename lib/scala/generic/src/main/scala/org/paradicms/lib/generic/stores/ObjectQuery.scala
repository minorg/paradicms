package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

final case class ObjectQuery(
                               filters: Option[ObjectFilters],
                               text: Option[String]
                             )

object ObjectQuery {
  def collection(collectionUri: Uri) =
    ObjectQuery(
      filters = Some(ObjectFilters.collection(collectionUri)),
      text = None
    )

  def institution(institutionUri: Uri) =
    ObjectQuery(
      filters = Some(ObjectFilters.institution(institutionUri)),
      text = None
    )

  def text(text: String) =
    new ObjectQuery(
      filters = None,
      text = Some(text)
    )
}
