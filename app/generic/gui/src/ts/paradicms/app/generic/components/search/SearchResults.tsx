import { useHistory, useLocation } from "react-router-dom";
import * as React from "react";
import { useState } from "react";
import * as SearchResultsInitialQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsInitialQuery.graphql";
import * as SearchResultsRefinementQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsRefinementQuery.graphql";
import { useApolloClient, useQuery } from "@apollo/react-hooks";
import * as ReactLoader from "react-loader";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as qs from "qs";
import {
  SearchResultsInitialQuery,
  SearchResultsInitialQuery_objects_collections,
  SearchResultsInitialQuery_objects_institutions,
  SearchResultsInitialQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsInitialQuery";
import {
  initialSearchResultsState,
  SearchResultsState
} from "paradicms/app/generic/components/search/SearchResultsState";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import {
  SearchResultsRefinementQuery,
  SearchResultsRefinementQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsRefinementQuery";
import { SearchResultsSummary } from "paradicms/app/generic/components/search/SearchResultsSummary";
import * as _ from "lodash";
import { Grid, Link, makeStyles } from "@material-ui/core";

const OBJECTS_PER_PAGE = 10;

const useStyles = makeStyles((theme) => ({
  noObjects: {
    textAlign: "center"
  },
  objectFacets: {
    borderLeft: "solid 1px",
    borderTop: "solid 1px"
  }
}));

export const SearchResults: React.FunctionComponent = () => {
  const history = useHistory();

  const location = useLocation();
  const locationObjectQuery: ObjectQuery = qs.parse(location.search.substring(1));

  const [state, setState] = useState<SearchResultsState>(initialSearchResultsState(locationObjectQuery));
  // console.debug("state: " + JSON.stringify(state));

  const initialVariables: SearchResultsInitialQueryVariables = {
    limit: OBJECTS_PER_PAGE,
    offset: 0,
    queryWithFilters: locationObjectQuery,
    queryWithoutFilters: {
      text: locationObjectQuery.text
    }
  };
  const {data: initialData, error: initialError} = useQuery<
    SearchResultsInitialQuery,
    SearchResultsInitialQueryVariables
    >(SearchResultsInitialQueryDocument, {
    variables: initialVariables,
  });

  // Don't need this until later, but every hook must be called on every render.
  const apolloClient = useApolloClient();

  const classes = useStyles();

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    console.debug("queried initial with " + JSON.stringify(initialVariables) + ", waiting on data");
    return <ReactLoader loaded={false} />;
  }

  const onLoadedData = (kwds: {data: SearchResultsRefinementQuery, objectQuery: ObjectQuery, objectsPage: number}) => {
    const {data, objectQuery, objectsPage} = kwds;
    
    const collectionsByUri: {[index: string]: SearchResultsInitialQuery_objects_collections} = {};
    data.objects.collections.forEach(objectCollection => collectionsByUri[objectCollection.uri] = objectCollection);
    const institutionsByUri: {[index: string]: SearchResultsInitialQuery_objects_institutions} = {};
    data.objects.institutions.forEach(objectInstitution => institutionsByUri[objectInstitution.uri] = objectInstitution);

    setState(prevState => ({
        objects: data.objects.objectsWithContext.map(objectWithContext => {
          const collection = collectionsByUri[objectWithContext.collectionUri]!;
          const institution = institutionsByUri[objectWithContext.institutionUri]!;
          const { rights: objectRights, ...otherObjectProps } = objectWithContext.object;
          const rights = objectRights
            ? objectRights
            : collection.rights
              ? collection.rights
              : institution.rights;
          return {
            collectionName: collection.name,
            collectionUri: collection.uri,
            institutionName: institution.name,
            institutionUri: institution.uri,
            rights: rights ? rights.text : undefined,
            ...otherObjectProps,
          };
        }),
        objectQuery,
        objectsCount: data.objectsCount,
        objectsPage
      }));
  };

  if (state.objectsCount === -1) {
    // First time we've seen initialData
    console.debug("first check of initial data, loading");
    onLoadedData({data: initialData, objectQuery: locationObjectQuery, objectsPage: 0});
    return <ReactLoader loaded={false}/>;
  }
  
  const searchText = state.objectQuery.text!;

  const onObjectsPageRequest = (page: number) => {
    console.info("request page " + page);
    const query = state.objectQuery;
    apolloClient.query<SearchResultsRefinementQuery, SearchResultsRefinementQueryVariables>({
      query: SearchResultsRefinementQueryDocument,
      variables: {limit: OBJECTS_PER_PAGE, offset: page * OBJECTS_PER_PAGE, query}
    }).then(({data}) => {
      onLoadedData({data, objectQuery: query, objectsPage: page});
    });
  };

  const onChangeObjectQuery = (newQuery: ObjectQuery) => {
    console.info("change query from " + JSON.stringify(state.objectQuery) + " to " + JSON.stringify(newQuery));
    if (!_.isEqual(locationObjectQuery, newQuery)) {
      // console.debug("pushing " + Hrefs.search(newQuery));
      history.push(Hrefs.search(newQuery));
    }
    apolloClient.query<SearchResultsRefinementQuery, SearchResultsRefinementQueryVariables>({
      query: SearchResultsRefinementQueryDocument,
      variables: {limit: OBJECTS_PER_PAGE, offset: 0, query: newQuery}
    }).then(({data}) => {
      onLoadedData({data, objectQuery: newQuery, objectsPage: 0});
    });
  };

  if (!_.isEqual(locationObjectQuery, state.objectQuery)) {
    console.debug("history has changed: location query=" + JSON.stringify(locationObjectQuery) + ", state query=" + JSON.stringify(state.objectQuery));
    onChangeObjectQuery(locationObjectQuery);
  }

  // New search in the navbar search form
  const onNewSearch = (text: string) => onChangeObjectQuery({text});

  return (
    <Frame
      breadcrumbItems={[
        <Link href={Hrefs.home}>Home</Link>,
        <Link href={Hrefs.search(state.objectQuery)}>
          Search: <i>{searchText}</i>
        </Link>
      ]}
      cardTitle={
        <React.Fragment>
          Search: <i>{searchText}</i>
        </React.Fragment>
      }
      documentTitle={"Search results: " + searchText}
      onSearch={onNewSearch}
    >
      <Grid direction="column">
        {state.objects.length ?
          <Grid item>
            <SearchResultsSummary objectsPerPage={OBJECTS_PER_PAGE} state={state}/>
          </Grid>
          : null}
        <Grid item>
          <Grid container>
            <Grid item xs={10}>
              {state.objects.length ?
                <ObjectsGallery
                  objects={state.objects}
                  currentPage={state.objectsPage}
                  maxPage={Math.ceil(state.objectsCount / OBJECTS_PER_PAGE) - 1}
                  onPageRequest={onObjectsPageRequest}
                /> :
                <h4 className={classes.noObjects}>No matching objects found.</h4>
              }
            </Grid>
            <Grid item className={classes.objectFacets} xs={2}>
              <ObjectFacets
                facets={initialData.objectFacets.facets}
                onChange={onChangeObjectQuery}
                query={state.objectQuery}
              />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Frame>
  );
};
