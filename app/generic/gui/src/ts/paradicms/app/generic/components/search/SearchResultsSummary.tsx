import { SearchResultsState } from "paradicms/app/generic/components/search/SearchResultsState";
import * as React from "react";

export const SearchResultsSummary: React.FunctionComponent<{objectsPerPage: number, state: SearchResultsState}> = ({objectsPerPage, state}) => {
  return (
    <p className="muted">
      Showing objects {(state.objectsPage * objectsPerPage) + 1} &mdash; {(state.objectsPage * objectsPerPage) + state.objects.length} of {state.objectsCount}.
    </p>
  );
}
