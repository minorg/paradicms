import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import * as SearchResultsInitialQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsInitialQuery.graphql";
import * as SearchResultsRefinementQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsRefinementQuery.graphql";
import { useApolloClient, useQuery } from "@apollo/react-hooks";
import * as ReactLoader from "react-loader";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as queryString from "query-string";
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
import { BreadcrumbItem, Col, Container, Row } from "reactstrap";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import {
  SearchResultsRefinementQuery,
  SearchResultsRefinementQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsRefinementQuery";

const OBJECTS_PER_PAGE = 10;

export const SearchResults: React.FunctionComponent<RouteComponentProps> = ({location}) => {
  const initialObjectQuery: ObjectQuery = queryString.parse(location.search);

  const [state, setState] = useState<SearchResultsState>(initialSearchResultsState(initialObjectQuery));

  const {data: initialData, error: initialError} = useQuery<
    SearchResultsInitialQuery,
    SearchResultsInitialQueryVariables
    >(SearchResultsInitialQueryDocument, {
    variables: {
      limit: OBJECTS_PER_PAGE,
      offset: 0,
      query: initialObjectQuery
    },
  });

  // Don't need this until later, but every hook must be called on every render.
  const apolloClient = useApolloClient();

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    return <ReactLoader loaded={false} />;
  }

  const onLoadedData = (kwds: {data: SearchResultsRefinementQuery, objectQuery: ObjectQuery, objectsPage: number}) => {
    const {data, objectQuery, objectsPage} = kwds;
    const collectionsByUri: {[index: string]: SearchResultsInitialQuery_objects_collections} = {};
    data.objects.collections.forEach(objectCollection => collectionsByUri[objectCollection.uri] = objectCollection);
    const institutionsByUri: {[index: string]: SearchResultsInitialQuery_objects_institutions} = {};
    data.objects.institutions.forEach(objectInstitution => institutionsByUri[objectInstitution.uri] = objectInstitution);

    // Having this as a separate function creates a stale closure of an old setState.
    setState(prevState => {
      return {
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
      }
    });
  };

  if (state.objectsCount === -1) {
    // First time we've seen initialData
    onLoadedData({data: initialData, objectQuery: initialObjectQuery, objectsPage: 0});
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
    apolloClient.query<SearchResultsRefinementQuery, SearchResultsRefinementQueryVariables>({
      query: SearchResultsRefinementQueryDocument,
      variables: {limit: OBJECTS_PER_PAGE, offset: 0, query: newQuery}
    }).then(({data}) => {
      onLoadedData({data, objectQuery: newQuery, objectsPage: 0});
    });
  };

  return (
    <Frame
      breadcrumbItems={
        <React.Fragment>
          <BreadcrumbItem>
            <Link to={Hrefs.home}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem>
            <Link to={Hrefs.search(state.objectQuery)}>
              Search: <i>{searchText}</i>&nbsp;
            </Link>
          </BreadcrumbItem>
        </React.Fragment>
      }
      cardTitle={
        <React.Fragment>
          Search: <i>{searchText}</i>&nbsp;({state.objectsCount} results)
        </React.Fragment>
      }
      documentTitle={"Search results: " + searchText}
    >
      <Container fluid>
        <Row>
          <Col xs="10">
            <ObjectsGallery
              objects={state.objects}
              currentPage={state.objectsPage}
              maxPage={Math.ceil(state.objectsCount / OBJECTS_PER_PAGE)}
              onPageRequest={onObjectsPageRequest}
            />
          </Col>
          <Col>
            <ObjectFacets
              facets={initialData.objectFacets.facets}
              onChange={onChangeObjectQuery}
              query={state.objectQuery}
            />
          </Col>
        </Row>
      </Container>
    </Frame>
  );
};
