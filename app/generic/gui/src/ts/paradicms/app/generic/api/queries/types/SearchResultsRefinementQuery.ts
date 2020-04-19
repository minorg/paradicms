/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

import { ObjectQuery } from "./../../graphqlGlobalTypes";

// ====================================================
// GraphQL query operation: SearchResultsRefinementQuery
// ====================================================

export interface SearchResultsRefinementQuery_objects_collections_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsRefinementQuery_objects_collections {
  __typename: "Collection";
  name: string;
  rights: SearchResultsRefinementQuery_objects_collections_rights | null;
  uri: string;
}

export interface SearchResultsRefinementQuery_objects_institutions_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsRefinementQuery_objects_institutions {
  __typename: "Institution";
  name: string;
  rights: SearchResultsRefinementQuery_objects_institutions_rights | null;
  uri: string;
}

export interface SearchResultsRefinementQuery_objects_objectsWithContext_object_thumbnail {
  __typename: "Image";
  url: string;
}

export interface SearchResultsRefinementQuery_objects_objectsWithContext_object_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsRefinementQuery_objects_objectsWithContext_object {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: SearchResultsRefinementQuery_objects_objectsWithContext_object_thumbnail | null;
  uri: string;
  rights: SearchResultsRefinementQuery_objects_objectsWithContext_object_rights | null;
}

export interface SearchResultsRefinementQuery_objects_objectsWithContext {
  __typename: "ObjectWithContext";
  collectionUri: string;
  institutionUri: string;
  object: SearchResultsRefinementQuery_objects_objectsWithContext_object;
}

export interface SearchResultsRefinementQuery_objects {
  __typename: "GetObjectsResult";
  collections: SearchResultsRefinementQuery_objects_collections[];
  institutions: SearchResultsRefinementQuery_objects_institutions[];
  objectsWithContext: SearchResultsRefinementQuery_objects_objectsWithContext[];
}

export interface SearchResultsRefinementQuery {
  objects: SearchResultsRefinementQuery_objects;
}

export interface SearchResultsRefinementQueryVariables {
  limit: number;
  offset: number;
  query: ObjectQuery;
}
