package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

final case class ObjectsQuery(
                               collectionUri: Option[Uri],
                               institutionUri: Option[Uri],
                               text: Option[String]
                             )

object ObjectsQuery {
  // Use overloads instead of default parameters to get around Sangria limitations.
  def collection(collectionUri: Uri) =
    new ObjectsQuery(collectionUri = Some(collectionUri), institutionUri = None, text = None)

  def text(text: String) =
    new ObjectsQuery(collectionUri = None, institutionUri = None, text = Some(text))
}