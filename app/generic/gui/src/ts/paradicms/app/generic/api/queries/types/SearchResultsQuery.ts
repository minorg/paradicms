/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

// ====================================================
// GraphQL query operation: SearchResultsQuery
// ====================================================

export interface SearchResultsQuery_matchingObjects_collections_rights {
  __typename: "Rights";
  text: string;
}

export interface SearchResultsQuery_matchingObjects_collections {
  __typename: "Collection";
  name: string;
  rights: SearchResultsQuery_matchingObjects_collections_rights | null;
  uri: string;
}

export interface SearchResultsQuery_matchingObjects_institutions_rights {
  __typename: "Rights";
  text: string;
}

export interface SearchResultsQuery_matchingObjects_institutions {
  __typename: "Institution";
  name: string;
  rights: SearchResultsQuery_matchingObjects_institutions_rights | null;
  uri: string;
}

export interface SearchResultsQuery_matchingObjects_objects_object_thumbnail {
  __typename: "Image";
  url: string;
}

export interface SearchResultsQuery_matchingObjects_objects_object_rights {
  __typename: "Rights";
  text: string;
}

export interface SearchResultsQuery_matchingObjects_objects_object {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: SearchResultsQuery_matchingObjects_objects_object_thumbnail | null;
  uri: string;
  rights: SearchResultsQuery_matchingObjects_objects_object_rights | null;
}

export interface SearchResultsQuery_matchingObjects_objects {
  __typename: "MatchingObject";
  collectionUri: string;
  institutionUri: string;
  object: SearchResultsQuery_matchingObjects_objects_object;
}

export interface SearchResultsQuery_matchingObjects {
  __typename: "MatchingObjects";
  collections: SearchResultsQuery_matchingObjects_collections[];
  institutions: SearchResultsQuery_matchingObjects_institutions[];
  objects: SearchResultsQuery_matchingObjects_objects[];
}

export interface SearchResultsQuery {
  matchingObjects: SearchResultsQuery_matchingObjects;
  matchingObjectsCount: number;
}

export interface SearchResultsQueryVariables {
  limit: number;
  offset: number;
  text: string;
}
