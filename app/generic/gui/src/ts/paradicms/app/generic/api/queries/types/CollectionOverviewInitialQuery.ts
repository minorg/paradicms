/* tslint:disable */
/* eslint-disable */
// This file was automatically generated and should not be edited.

import { ObjectQuery } from "./../../graphqlGlobalTypes";

// ====================================================
// GraphQL query operation: CollectionOverviewInitialQuery
// ====================================================

export interface CollectionOverviewInitialQuery_collectionByUri_objectFacets {
  __typename: "ObjectFacets";
  culturalContexts: string[];
  materials: string[];
  spatials: string[];
  subjects: string[];
  techniques: string[];
  temporals: string[];
  types: string[];
}

export interface CollectionOverviewInitialQuery_collectionByUri_objects_thumbnail {
  __typename: "Image";
  height: number | null;
  url: string;
  width: number | null;
}

export interface CollectionOverviewInitialQuery_collectionByUri_objects {
  __typename: "Object";
  description: string | null;
  title: string;
  thumbnail: CollectionOverviewInitialQuery_collectionByUri_objects_thumbnail | null;
  uri: string;
}

export interface CollectionOverviewInitialQuery_collectionByUri_rights {
  __typename: "Rights";
  holder: string | null;
  license: string | null;
  statementUri: string | null;
  text: string | null;
}

export interface CollectionOverviewInitialQuery_collectionByUri {
  __typename: "Collection";
  description: string | null;
  name: string;
  objectFacets: CollectionOverviewInitialQuery_collectionByUri_objectFacets;
  objects: CollectionOverviewInitialQuery_collectionByUri_objects[];
  objectsCount: number;
  rights: CollectionOverviewInitialQuery_collectionByUri_rights | null;
}

export interface CollectionOverviewInitialQuery_institutionByUri_rights {
  __typename: "Rights";
  holder: string | null;
  license: string | null;
  statementUri: string | null;
  text: string | null;
}

export interface CollectionOverviewInitialQuery_institutionByUri {
  __typename: "Institution";
  name: string;
  rights: CollectionOverviewInitialQuery_institutionByUri_rights | null;
}

export interface CollectionOverviewInitialQuery {
  collectionByUri: CollectionOverviewInitialQuery_collectionByUri;
  institutionByUri: CollectionOverviewInitialQuery_institutionByUri;
}

export interface CollectionOverviewInitialQueryVariables {
  collectionUri: string;
  institutionUri: string;
  limit: number;
  query?: ObjectQuery | null;
}
