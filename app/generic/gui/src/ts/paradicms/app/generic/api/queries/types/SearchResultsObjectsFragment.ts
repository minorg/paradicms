/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

// ====================================================
// GraphQL fragment: SearchResultsObjectsFragment
// ====================================================

export interface SearchResultsObjectsFragment_collections_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsObjectsFragment_collections {
  __typename: "Collection";
  name: string;
  rights: SearchResultsObjectsFragment_collections_rights | null;
  uri: string;
}

export interface SearchResultsObjectsFragment_institutions_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsObjectsFragment_institutions {
  __typename: "Institution";
  name: string;
  rights: SearchResultsObjectsFragment_institutions_rights | null;
  uri: string;
}

export interface SearchResultsObjectsFragment_objectsWithContext_object_thumbnail {
  __typename: "Image";
  height: number | null;
  url: string;
  width: number | null;
}

export interface SearchResultsObjectsFragment_objectsWithContext_object_rights {
  __typename: "Rights";
  text: string | null;
}

export interface SearchResultsObjectsFragment_objectsWithContext_object {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: SearchResultsObjectsFragment_objectsWithContext_object_thumbnail | null;
  uri: string;
  rights: SearchResultsObjectsFragment_objectsWithContext_object_rights | null;
}

export interface SearchResultsObjectsFragment_objectsWithContext {
  __typename: "ObjectWithContext";
  collectionUri: string;
  institutionUri: string;
  object: SearchResultsObjectsFragment_objectsWithContext_object;
}

export interface SearchResultsObjectsFragment {
  __typename: "GetObjectsResult";
  collections: SearchResultsObjectsFragment_collections[];
  institutions: SearchResultsObjectsFragment_institutions[];
  objectsWithContext: SearchResultsObjectsFragment_objectsWithContext[];
}
