import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import * as SearchResultsInitialQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsInitialQuery.graphql";
import * as SearchResultsRefinementQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsRefinementQuery.graphql";
import { useLazyQuery, useQuery } from "@apollo/react-hooks";
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
import {
  SearchResultsRefinementQuery,
  SearchResultsRefinementQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsRefinementQuery";
import { BreadcrumbItem, Col, Container, Row } from "reactstrap";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import * as invariant from "invariant";

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

  // This isn't used until later, but all hooks must be rendered on every call to render().
  const [
    refinementQuery,
    {data: refinementData},
  ] = useLazyQuery<
    SearchResultsRefinementQuery,
    SearchResultsRefinementQueryVariables
    >(SearchResultsRefinementQueryDocument);

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    return <ReactLoader loaded={false} />;
  }

  if (state.loading) {
    let newData: SearchResultsRefinementQuery | undefined;
    if (refinementData) {
      newData = refinementData;
    } else if (!state.rendered) {
      newData = initialData;
    }

    if (newData) {
      const collectionsByUri: {[index: string]: SearchResultsInitialQuery_objects_collections} = {};
      newData.objects.collections.forEach(objectCollection => collectionsByUri[objectCollection.uri] = objectCollection);
      const institutionsByUri: {[index: string]: SearchResultsInitialQuery_objects_institutions} = {};
      newData.objects.institutions.forEach(objectInstitution => institutionsByUri[objectInstitution.uri] = objectInstitution);

      // Having this as a separate function creates a stale closure of an old setState.
      setState(prevState => {
        if (!prevState.loading) {
          throw new EvalError();
        }
        if (!newData) {
          throw new EvalError();
        }
        return {
          loading: null,
          rendered: {
            objects: newData.objects.objectsWithContext.map(objectWithContext => {
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
            objectQuery: prevState.loading?.objectQuery,
            objectsCount: newData.objectsCount,
            objectsPage: prevState.loading.objectsPage
          }
        }
      });
    }
  }

  if (!state.rendered) {
    return <ReactLoader loaded={false}/>;
  }

  const searchText = state.rendered.objectQuery.text!;

  const onObjectsPageRequest = (page: number) => {
    console.info("request page " + page);
    if (state.loading) {
      console.warn("already loading, ignoring page change request");
      return;
    }
    setState(prevState => {
      invariant(!prevState.loading, "cannot already be loading");
      invariant(prevState.rendered, "must have already rendered in order to change object pages");
      return {
        loading: {objectsPage: page, objectQuery: prevState.rendered!.objectQuery},
        rendered: prevState.rendered
      };
    });
    refinementQuery({
      variables: {limit: OBJECTS_PER_PAGE, offset: page * OBJECTS_PER_PAGE, query: state.rendered!.objectQuery},
    });
  };

  const onChangeObjectQuery = (newQuery: ObjectQuery) => {
    console.info("change query from " + JSON.stringify(state.rendered?.objectQuery) + " to " + JSON.stringify(newQuery));
    if (state.loading) {
      console.warn("already loading, ignoring query change request");
      return;
    }
    // Start over at the first page.
    setState(prevState => {
      invariant(!prevState.loading, "cannot already be loading");
      invariant(prevState.rendered, "must have already rendered in order to change object pages");
      return {
        loading: {objectsPage: 0, objectQuery: newQuery},
        rendered: prevState.rendered
      };
    });
    refinementQuery({
      variables: {limit: OBJECTS_PER_PAGE, offset: 0, query: newQuery}
    })
  };

  return (
    <Frame
      breadcrumbItems={
        <React.Fragment>
          <BreadcrumbItem>
            <Link to={Hrefs.home}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem>
            <Link to={Hrefs.search(state.rendered.objectQuery)}>
              Search: <i>{searchText}</i>&nbsp;
            </Link>
          </BreadcrumbItem>
        </React.Fragment>
      }
      cardTitle={
        <React.Fragment>
          Search: <i>{searchText}</i>&nbsp;({state.rendered.objectsCount} results)
        </React.Fragment>
      }
      documentTitle={"Search results: " + searchText}
    >
      <Container fluid>
        <Row>
          <Col xs="10">
            <ObjectsGallery
              objects={state.rendered.objects}
              currentPage={state.rendered.objectsPage}
              maxPage={Math.ceil(state.rendered.objectsCount / OBJECTS_PER_PAGE)}
              onPageRequest={onObjectsPageRequest}
            />
          </Col>
          <Col>
            <ObjectFacets
              facets={initialData.objectFacets.facets}
              onChange={onChangeObjectQuery}
              query={state.rendered.objectQuery}
            />
          </Col>
        </Row>
      </Container>
    </Frame>
  );
};
