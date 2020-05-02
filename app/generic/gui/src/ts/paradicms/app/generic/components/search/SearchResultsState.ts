import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import { ObjectSummary } from "paradicms/app/generic/models/object/ObjectSummary";

export interface SearchResultsState {
  objects: ObjectSummary[];
  objectsCount: number;
  objectsPage: number;
  objectQuery: ObjectQuery;
}

export const initialSearchResultsState = (initialObjectQuery: ObjectQuery): SearchResultsState => ({
  objects: [],
  objectsCount: -1,
  objectsPage: -1,
  objectQuery: initialObjectQuery
});

