/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

import { ObjectQuery } from "./../../graphqlGlobalTypes";

// ====================================================
// GraphQL query operation: SearchResultsInitialQuery
// ====================================================

export interface SearchResultsInitialQuery_objectFacets_facets {
  __typename: "ObjectFacets";
  subjects: string[];
  types: string[];
}

export interface SearchResultsInitialQuery_objectFacets {
  __typename: "GetObjectFacetsResult";
  facets: SearchResultsInitialQuery_objectFacets_facets;
}

export interface SearchResultsInitialQuery_objects_collections_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsInitialQuery_objects_collections {
  __typename: "Collection";
  name: string;
  rights: SearchResultsInitialQuery_objects_collections_rights | null;
  uri: string;
}

export interface SearchResultsInitialQuery_objects_institutions_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsInitialQuery_objects_institutions {
  __typename: "Institution";
  name: string;
  rights: SearchResultsInitialQuery_objects_institutions_rights | null;
  uri: string;
}

export interface SearchResultsInitialQuery_objects_objectsWithContext_object_thumbnail {
  __typename: "Image";
  url: string;
}

export interface SearchResultsInitialQuery_objects_objectsWithContext_object_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsInitialQuery_objects_objectsWithContext_object {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: SearchResultsInitialQuery_objects_objectsWithContext_object_thumbnail | null;
  uri: string;
  rights: SearchResultsInitialQuery_objects_objectsWithContext_object_rights | null;
}

export interface SearchResultsInitialQuery_objects_objectsWithContext {
  __typename: "ObjectWithContext";
  collectionUri: string;
  institutionUri: string;
  object: SearchResultsInitialQuery_objects_objectsWithContext_object;
}

export interface SearchResultsInitialQuery_objects {
  __typename: "GetObjectsResult";
  collections: SearchResultsInitialQuery_objects_collections[];
  institutions: SearchResultsInitialQuery_objects_institutions[];
  objectsWithContext: SearchResultsInitialQuery_objects_objectsWithContext[];
}

export interface SearchResultsInitialQuery {
  objectFacets: SearchResultsInitialQuery_objectFacets;
  objects: SearchResultsInitialQuery_objects;
  objectsCount: number;
}

export interface SearchResultsInitialQueryVariables {
  limit: number;
  offset: number;
  queryWithFilters: ObjectQuery;
  queryWithoutFilters: ObjectQuery;
}
