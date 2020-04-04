/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

import { ObjectsQuery } from "./../../graphqlGlobalTypes";

// ====================================================
// GraphQL query operation: SearchResultsQuery
// ====================================================

export interface SearchResultsQuery_objects_collections_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsQuery_objects_collections {
  __typename: "Collection";
  name: string;
  rights: SearchResultsQuery_objects_collections_rights | null;
  uri: string;
}

export interface SearchResultsQuery_objects_facets {
  __typename: "ObjectFacets";
  subjects: string[];
  types: string[];
}

export interface SearchResultsQuery_objects_institutions_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsQuery_objects_institutions {
  __typename: "Institution";
  name: string;
  rights: SearchResultsQuery_objects_institutions_rights | null;
  uri: string;
}

export interface SearchResultsQuery_objects_objectsWithContext_object_thumbnail {
  __typename: "Image";
  url: string;
}

export interface SearchResultsQuery_objects_objectsWithContext_object_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsQuery_objects_objectsWithContext_object {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: SearchResultsQuery_objects_objectsWithContext_object_thumbnail | null;
  uri: string;
  rights: SearchResultsQuery_objects_objectsWithContext_object_rights | null;
}

export interface SearchResultsQuery_objects_objectsWithContext {
  __typename: "ObjectWithContext";
  collectionUri: string;
  institutionUri: string;
  object: SearchResultsQuery_objects_objectsWithContext_object;
}

export interface SearchResultsQuery_objects {
  __typename: "GetObjectsResult";
  collections: SearchResultsQuery_objects_collections[];
  facets: SearchResultsQuery_objects_facets;
  institutions: SearchResultsQuery_objects_institutions[];
  objectsWithContext: SearchResultsQuery_objects_objectsWithContext[];
}

export interface SearchResultsQuery {
  objects: SearchResultsQuery_objects;
  objectsCount: number;
}

export interface SearchResultsQueryVariables {
  limit: number;
  offset: number;
  query: ObjectsQuery;
}
