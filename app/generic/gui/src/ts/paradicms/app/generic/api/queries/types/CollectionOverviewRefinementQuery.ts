/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

import { ObjectQuery } from "./../../graphqlGlobalTypes";

// ====================================================
// GraphQL query operation: CollectionOverviewRefinementQuery
// ====================================================

export interface CollectionOverviewRefinementQuery_collectionByUri_objects_thumbnail {
  __typename: "Image";
  height: number | null;
  url: string;
  width: number | null;
}

export interface CollectionOverviewRefinementQuery_collectionByUri_objects {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: CollectionOverviewRefinementQuery_collectionByUri_objects_thumbnail | null;
  uri: string;
}

export interface CollectionOverviewRefinementQuery_collectionByUri {
  __typename: "Collection";
  objects: CollectionOverviewRefinementQuery_collectionByUri_objects[];
  objectsCount: number;
}

export interface CollectionOverviewRefinementQuery {
  collectionByUri: CollectionOverviewRefinementQuery_collectionByUri;
}

export interface CollectionOverviewRefinementQueryVariables {
  collectionUri: string;
  limit: number;
  offset: number;
  query: ObjectQuery;
}
