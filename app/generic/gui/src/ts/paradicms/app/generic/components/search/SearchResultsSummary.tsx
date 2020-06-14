import { SearchResultsState } from "paradicms/app/generic/components/search/SearchResultsState";
import * as React from "react";

export const SearchResultsSummary: React.FunctionComponent<{objectsPerPage: number, state: SearchResultsState}> = ({objectsPerPage, state}) => {
  return (
    <p className="muted">
      Showing objects <span data-cy="start-object-index">{(state.objectsPage * objectsPerPage) + 1}</span> &mdash; <span data-cy="end-object-index">{(state.objectsPage * objectsPerPage) + state.objects.length}</span> of <span data-cy="objects-count">{state.objectsCount}</span>.
    </p>
  );
}
