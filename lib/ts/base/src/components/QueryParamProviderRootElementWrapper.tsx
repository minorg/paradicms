import * as React from "react";
import {Location, globalHistory} from "@reach/router";
import {QueryParamProvider} from "use-query-params";

export const QueryParamProviderRootElementWrapper: React.FunctionComponent<{
  element: React.ReactNode;
}> = ({element}) => (
  <Location>
    {({location}) => (
      <QueryParamProvider location={location} reachHistory={globalHistory}>
        {element}
      </QueryParamProvider>
    )}
  </Location>
);
