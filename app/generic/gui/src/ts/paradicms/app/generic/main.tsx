import { apolloClient } from "paradicms/app/generic/api/apolloClient";
import { NoRoute } from "paradicms/app/generic/components/error/NoRoute";
import { Home } from "paradicms/app/generic/components/home/Home";
import { Privacy } from "paradicms/app/generic/components/static/Privacy";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloProvider as ApolloHooksProvider } from "@apollo/react-hooks";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { ConsoleLogger, LoggerContext } from "@paradicms/base";
import { CollectionOverview } from "paradicms/app/generic/components/collection/CollectionOverview";
import { InstitutionOverview } from "paradicms/app/generic/components/institution/InstitutionOverview";
import { ObjectOverview } from "paradicms/app/generic/components/object/ObjectOverview";
import { SearchResults } from "paradicms/app/generic/components/search/SearchResults";
import { CssBaseline } from "@material-ui/core";

// Logger
const logger = new ConsoleLogger();

ReactDOM.render(
  <React.Fragment>
    <CssBaseline/>
    <ApolloProvider client={apolloClient}>
      <ApolloHooksProvider client={apolloClient}>
        <LoggerContext.Provider value={logger}>
          <BrowserRouter>
            <Switch>
              <Route exact path={Hrefs.home} component={Home} />
              <Route
                path="/institution/:institutionUri/collection/:collectionUri/object/:objectUri"
                component={ObjectOverview}
              />
              <Route
                path="/institution/:institutionUri/collection/:collectionUri"
                component={CollectionOverview}
              />
              <Route
                path="/institution/:institutionUri"
                component={InstitutionOverview}
              />
              <Route exact path={Hrefs.privacy} component={Privacy} />
              <Route exact path="/search" component={SearchResults} />
              <Route component={NoRoute} />
            </Switch>
          </BrowserRouter>
        </LoggerContext.Provider>
      </ApolloHooksProvider>
    </ApolloProvider>
  </React.Fragment>,
  document.getElementById("root")
);
