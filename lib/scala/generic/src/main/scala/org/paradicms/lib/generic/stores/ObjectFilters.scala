package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

/**
 * Object filters used by the API.
 *
 * The Sangria macro that derives input object types from case classes doesn't support default parameters,
 * which is why we have the factory methods below.
 */
final case class ObjectFilters(
                                collectionUris: Option[UriFacetFilter],
                                institutionUris: Option[UriFacetFilter],
                                spatials: Option[StringFacetFilter],
                                subjects: Option[StringFacetFilter],
                                temporals: Option[StringFacetFilter],
                                types: Option[StringFacetFilter]
                              )

object ObjectFilters {
  def collection(collectionUri: Uri) =
    create(collectionUris = Some(UriFacetFilter(exclude = None, include = Some(List(collectionUri)))))

  // Cannot be called apply, since Sangria considers that the substitute for the usual case class factory, per above.
  def create(
              collectionUris: Option[UriFacetFilter] = None,
              institutionUris: Option[UriFacetFilter] = None,
              spatials: Option[StringFacetFilter] = None,
              subjects: Option[StringFacetFilter] = None,
              temporals: Option[StringFacetFilter] = None,
              types: Option[StringFacetFilter] = None
            ): ObjectFilters =
    ObjectFilters(
      collectionUris = collectionUris,
      institutionUris = institutionUris,
      spatials = spatials,
      subjects = subjects,
      temporals = temporals,
      types = types
    )

  def institution(institutionUri: Uri) =
    create(institutionUris = Some(UriFacetFilter(exclude = None, include = Some(List(institutionUri)))))
}
