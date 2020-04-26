/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

//==============================================================
// START Enums and Input Objects
//==============================================================

export interface ObjectFilters {
  collectionUris?: UriFacetFilter | null;
  culturalContexts?: StringFacetFilter | null;
  institutionUris?: UriFacetFilter | null;
  materials?: StringFacetFilter | null;
  spatials?: StringFacetFilter | null;
  subjects?: StringFacetFilter | null;
  techniques?: StringFacetFilter | null;
  temporals?: StringFacetFilter | null;
  types?: StringFacetFilter | null;
}

export interface ObjectQuery {
  filters?: ObjectFilters | null;
  text?: string | null;
}

export interface StringFacetFilter {
  exclude?: string[] | null;
  include?: string[] | null;
}

export interface UriFacetFilter {
  exclude?: string[] | null;
  include?: string[] | null;
}

//==============================================================
// END Enums and Input Objects
//==============================================================
