/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

import { ObjectQuery } from "./../../graphqlGlobalTypes";

// ====================================================
// GraphQL query operation: CollectionOverviewFollowUpQuery
// ====================================================

export interface CollectionOverviewFollowUpQuery_collectionByUri_objects_thumbnail {
  __typename: "Image";
  url: string;
}

export interface CollectionOverviewFollowUpQuery_collectionByUri_objects {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: CollectionOverviewFollowUpQuery_collectionByUri_objects_thumbnail | null;
  uri: string;
}

export interface CollectionOverviewFollowUpQuery_collectionByUri {
  __typename: "Collection";
  objects: CollectionOverviewFollowUpQuery_collectionByUri_objects[];
}

export interface CollectionOverviewFollowUpQuery {
  collectionByUri: CollectionOverviewFollowUpQuery_collectionByUri;
}

export interface CollectionOverviewFollowUpQueryVariables {
  collectionUri: string;
  limit: number;
  offset: number;
  query: ObjectQuery;
}
