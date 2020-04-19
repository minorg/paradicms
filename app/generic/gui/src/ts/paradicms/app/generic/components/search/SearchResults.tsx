import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import * as SearchResultsInitialQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsInitialQuery.graphql";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import { ObjectSummary } from "paradicms/app/generic/components/object/ObjectSummary";
import { useQuery } from "@apollo/react-hooks";
import * as ReactLoader from "react-loader";
import { BreadcrumbItem, Col, Container, Row } from "reactstrap";
import { Link } from "react-router-dom";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as queryString from "query-string";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import {
  SearchResultsInitialQuery,
  SearchResultsInitialQuery_objects_collections,
  SearchResultsInitialQuery_objects_institutions,
  SearchResultsInitialQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsInitialQuery";
import * as invariant from "invariant";
import { SearchResultsObjectsFragment } from "paradicms/app/generic/api/queries/types/SearchResultsObjectsFragment";

const OBJECTS_PER_PAGE = 10;

export const SearchResults: React.FunctionComponent<RouteComponentProps> = ({location}) => {
  const initialQuery: ObjectQuery = queryString.parse(location.search);

  const [state, setState] = useState<{
    loadingPage: number | null;
    loadingQuery: ObjectQuery | null;
    renderedObjects: ObjectSummary[] | null;
    renderedPage: number | null;
    renderedQuery: ObjectQuery | null;
  }>({
    loadingPage: null,
    loadingQuery: initialQuery,
    renderedObjects: null,
    renderedPage: null,
    renderedQuery: null
  });
  console.info("State is ", JSON.stringify(state));

  const {data: initialData, error: initialError} = useQuery<
    SearchResultsInitialQuery,
    SearchResultsInitialQueryVariables
    >(SearchResultsInitialQueryDocument, {
    variables: {
      limit: OBJECTS_PER_PAGE,
      offset: 0,
      query: initialQuery
    },
  });

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    return <ReactLoader loaded={false} />;
  } else if (!state.renderedObjects) {
    // Have initial data and have setState with it, which hasn't gone through yet.
    invariant(!state.renderedQuery, "rendered objects and query must be in sync");
    return <ReactLoader loaded={false} />;
  }

  if (!state.renderedObjects || !state.renderedQuery || state.renderedPage === null) {
    throw new EvalError("rendered objects and query must be set here");
  }

  // const [
  //   refinementQuery,
  //   {data: refinementData},
  // ] = useLazyQuery<
  //   CollectionOverviewRefinementQuery,
  //   CollectionOverviewRefinementQueryVariables
  //   >(CollectionOverviewRefinementQueryDocument);
  
  const onObjectsLoaded = (
    objects: SearchResultsObjectsFragment
  ) => {
    const collectionsByUri: {[index: string]: SearchResultsInitialQuery_objects_collections} = {};
    objects.collections.forEach(objectCollection => collectionsByUri[objectCollection.uri] = objectCollection);
    const institutionsByUri: {[index: string]: SearchResultsInitialQuery_objects_institutions} = {};
    objects.institutions.forEach(objectInstitution => institutionsByUri[objectInstitution.uri] = objectInstitution);
    setState(prevState =>
      Object.assign({}, prevState, {
        // maxPage: Math.ceil(objectsCount / 10),
        objects: objects.objectsWithContext.map(objectWithContext => {
          const collection = collectionsByUri[objectWithContext.collectionUri]!;
          const institution = institutionsByUri[objectWithContext.institutionUri]!;
          const {rights: objectRights, ...otherObjectProps} = objectWithContext.object;
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
      }));
  };

  // if (refinementData) {
  //   onObjectsLoaded(refinementData.collectionByUri.objects);
  if (initialData) {
    onObjectsLoaded(initialData.objects);
  }
  
  const onObjectsPageRequest = (page: number) => {
    const renderedQuery = state.renderedQuery!;
    setState(prevState => ({ ...prevState, renderedPage: page, loadingQuery: renderedQuery }));
    //   refetch({limit: 10, offset: page * 10, query});
    // };
  };

  const searchText = state.renderedQuery.text;

  return (
    <Frame
      breadcrumbItems={
        <React.Fragment>
          <BreadcrumbItem>
            <Link to={Hrefs.home}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem>
            <Link to={Hrefs.search(state.renderedQuery)}>
              Search: <i>{searchText}</i>
            </Link>
          </BreadcrumbItem>
        </React.Fragment>
      }
      cardTitle={
        <React.Fragment>
          Search: <i>{searchText}</i>
        </React.Fragment>
      }
      documentTitle={"Search results: " + searchText}
    >
      <Container fluid>
        <Row>
          <Col xs="10">
            <ObjectsGallery
              objects={state.renderedObjects}
              currentPage={state.renderedPage}
              maxPage={Math.ceil(initialData.objectsCount / OBJECTS_PER_PAGE)}
              onPageRequest={onObjectsPageRequest}
            />
          </Col>
          <Col>
            <ObjectFacets
              facets={initialData.objectFacets.facets}
              onChange={(newQuery) => { return; }}
              query={state.renderedQuery}
            />
          </Col>
        </Row>
      </Container>
    </Frame>
  );
};
