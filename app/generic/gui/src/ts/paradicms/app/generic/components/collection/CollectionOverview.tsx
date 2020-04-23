import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import * as CollectionOverviewInitialQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewInitialQuery.graphql";
import * as CollectionOverviewRefinementQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewRefinementQuery.graphql";
import {
  CollectionOverviewInitialQuery,
  CollectionOverviewInitialQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewInitialQuery";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import { useApolloClient, useQuery } from "@apollo/react-hooks";
import * as ReactLoader from "react-loader";
import { InstitutionCollectionObjectOverview } from "paradicms/app/generic/components/frame/InstitutionCollectionObjectOverview";
import { RightsTable } from "paradicms/app/generic/components/rights/RightsTable";
import { Col, Container, Row } from "reactstrap";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import {
  CollectionOverviewRefinementQuery,
  CollectionOverviewRefinementQuery_collectionByUri,
  CollectionOverviewRefinementQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewRefinementQuery";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import {
  initialSearchResultsState,
  SearchResultsState
} from "paradicms/app/generic/components/search/SearchResultsState";
import { SearchResultsSummary } from "paradicms/app/generic/components/search/SearchResultsSummary";

const OBJECTS_PER_PAGE = 20;

export const CollectionOverview: React.FunctionComponent<RouteComponentProps<{
  collectionUri: string;
  institutionUri: string;
}>> = ({match}) => {
  const collectionUri = decodeURIComponent(match.params.collectionUri);
  const institutionUri = decodeURIComponent(match.params.institutionUri);

  const initialObjectQuery: ObjectQuery = {
    filters: {
      collectionUris: { include: [collectionUri] }
    }
  };

  const [state, setState] = useState<SearchResultsState>(initialSearchResultsState(initialObjectQuery));

  const {data: initialData, error: initialError} = useQuery<
    CollectionOverviewInitialQuery,
    CollectionOverviewInitialQueryVariables
  >(CollectionOverviewInitialQueryDocument, {
    variables: {
      collectionUri,
      institutionUri,
      limit: OBJECTS_PER_PAGE
    },
  });

  // Don't need this until later, but every hook must be called on every render.
  const apolloClient = useApolloClient();

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    return <ReactLoader loaded={false} />;
  }

  const onLoadedData = (kwds: {data: CollectionOverviewRefinementQuery_collectionByUri, objectQuery: ObjectQuery, objectsPage: number}) => {
    const {data, objectQuery, objectsPage} = kwds;
    setState(prevState => {
      return {
        objects: data.objects.map(object_ => ({
          collectionUri,
          institutionUri,
          rights: null,
          ...object_,
        })),
        objectsCount: data.objectsCount,
        objectQuery,
        objectsPage
      };
    });
  };

  if (state.objectsCount === -1) {
    // First time we've seen initialData
    onLoadedData({data: initialData.collectionByUri, objectQuery: initialObjectQuery, objectsPage: 0});
    return <ReactLoader loaded={false}/>;
  }

  const onObjectsPageRequest = (page: number) => {
    console.info("request page " + page);
    const query = state.objectQuery;
    apolloClient.query<CollectionOverviewRefinementQuery, CollectionOverviewRefinementQueryVariables>({
      query: CollectionOverviewRefinementQueryDocument,
      variables: {collectionUri, limit: OBJECTS_PER_PAGE, offset: page * OBJECTS_PER_PAGE, query}
    }).then(({data}) => {
      onLoadedData({data: data.collectionByUri, objectQuery: query, objectsPage: page});
    });
  };

  const onChangeObjectQuery = (newQuery: ObjectQuery) => {
    console.info("change query from " + JSON.stringify(state.objectQuery) + " to " + JSON.stringify(newQuery));
    apolloClient.query<CollectionOverviewRefinementQuery, CollectionOverviewRefinementQueryVariables>({
      query: CollectionOverviewRefinementQueryDocument,
      variables: {collectionUri, limit: OBJECTS_PER_PAGE, offset: 0, query: newQuery}
    }).then(({data}) => {
      onLoadedData({data: data.collectionByUri, objectQuery: newQuery, objectsPage: 0});
    });
  };

  const rights = initialData
    ? initialData.collectionByUri.rights
      ? initialData.collectionByUri.rights
      : initialData.institutionByUri.rights
    : undefined;

  return (
    <InstitutionCollectionObjectOverview
      collectionName={initialData.collectionByUri.name}
      collectionUri={collectionUri}
      institutionName={initialData.institutionByUri.name}
      institutionUri={institutionUri}
      title={initialData.collectionByUri.name}
    >
      <Container fluid>
        {rights && state.objects.length ? (
          <Row className="pb-4">
            <Col xs="10">
              <RightsTable rights={rights} />
            </Col>
          </Row>
        ) : null}
        {state.objects.length ?
          <React.Fragment>
            <Row>
              <Col>
                <SearchResultsSummary objectsPerPage={OBJECTS_PER_PAGE} state={state}/>
              </Col>
            </Row>
            <Row>
            </Row>
          </React.Fragment> : null}
        <Row>
          <Col xs={10}>
            {state.objects.length ?
              <ObjectsGallery
                currentPage={state.objectsPage}
                maxPage={Math.ceil(state.objectsCount / OBJECTS_PER_PAGE)}
                objects={state.objects}
                onPageRequest={onObjectsPageRequest}
              /> :
              <h4 className="text-center">No matching objects found.</h4>
            }
          </Col>
          <Col className="border-left border-top" xs={2}>
            <ObjectFacets
              facets={initialData.collectionByUri.objectFacets}
              onChange={onChangeObjectQuery}
              query={state.objectQuery}
            />
          </Col>
        </Row>
      </Container>
    </InstitutionCollectionObjectOverview>
  );
};
