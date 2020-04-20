import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import { ObjectSummary } from "paradicms/app/generic/components/object/ObjectSummary";

interface SearchResultsLoadingState {
  objectsPage: number;
  objectQuery: ObjectQuery;
}

interface SearchResultsRenderedState {
  objects: ObjectSummary[];
  objectsCount: number;
  objectsPage: number;
  objectQuery: ObjectQuery;
}

export interface SearchResultsState {
  loading: SearchResultsLoadingState | null;
  rendered: SearchResultsRenderedState | null;
}

export const initialSearchResultsState = (initialObjectQuery: ObjectQuery): SearchResultsState => ({
  loading: { objectsPage: 0, objectQuery: initialObjectQuery },
  rendered: null
});

