import "paradicms/app/generic/custom_bootstrap.scss";

import {apolloClient} from "paradicms/app/generic/api/apolloClient";
import {createBrowserHistory} from "history";
import {NoRoute} from "paradicms/app/generic/components/error/NoRoute";
import {Home} from "paradicms/app/generic/components/home/Home";
import {Privacy} from "paradicms/app/generic/components/static/Privacy";
import {Hrefs} from "paradicms/app/generic/Hrefs";
import * as React from "react";
import * as ReactDOM from "react-dom";
import {ApolloProvider} from "react-apollo";
import {ApolloProvider as ApolloHooksProvider} from "@apollo/react-hooks";
import {Route, Router, Switch} from "react-router";
import {ConsoleLogger, LoggerContext} from "paradicms-base";
import {CollectionOverview} from "paradicms/app/generic/components/collection/CollectionOverview";
import {InstitutionOverview} from "paradicms/app/generic/components/institution/InstitutionOverview";
import {ObjectOverview} from "paradicms/app/generic/components/object/ObjectOverview";
import {SearchResults} from "paradicms/app/generic/components/search/SearchResults";

// Logger
const logger = new ConsoleLogger();

// Stores
const browserHistory = createBrowserHistory();

ReactDOM.render(
  <ApolloProvider client={apolloClient}>
    <ApolloHooksProvider client={apolloClient}>
      <LoggerContext.Provider value={logger}>
        <Router history={browserHistory}>
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
            <Route path="/search/:text" component={SearchResults} />
            <Route component={NoRoute} />
          </Switch>
        </Router>
      </LoggerContext.Provider>
    </ApolloHooksProvider>
  </ApolloProvider>,
  document.getElementById("root")
);
