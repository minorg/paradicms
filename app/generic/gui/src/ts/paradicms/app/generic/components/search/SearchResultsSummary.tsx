import { SearchResultsState } from "paradicms/app/generic/components/search/SearchResultsState";
import * as React from "react";

export const SearchResultsSummary: React.FunctionComponent<{objectsPerPage: number, state: SearchResultsState}> = ({objectsPerPage, state}) => {
  return (
    <p className="muted">
      Showing objects <span id="start-object-index">{(state.objectsPage * objectsPerPage) + 1}</span> &mdash; <span id="end-object-index">{(state.objectsPage * objectsPerPage) + state.objects.length}</span> of <span id="objects-count">{state.objectsCount}</span>.
    </p>
  );
}
