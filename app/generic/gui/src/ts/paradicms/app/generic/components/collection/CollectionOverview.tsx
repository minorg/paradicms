import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import * as CollectionOverviewInitialQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewInitialQuery.graphql";
import {
  CollectionOverviewInitialQuery,
  CollectionOverviewInitialQuery_collectionByUri_objects,
  CollectionOverviewInitialQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewInitialQuery";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import * as CollectionOverviewRefinementQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewRefinementQuery.graphql";
import { useLazyQuery, useQuery } from "@apollo/react-hooks";
import { ObjectSummary } from "paradicms/app/generic/components/object/ObjectSummary";
import * as ReactLoader from "react-loader";
import { InstitutionCollectionObjectOverview } from "paradicms/app/generic/components/frame/InstitutionCollectionObjectOverview";
import { RightsTable } from "paradicms/app/generic/components/rights/RightsTable";
import { Col, Container, Row } from "reactstrap";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import {
  CollectionOverviewRefinementQuery,
  CollectionOverviewRefinementQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewRefinementQuery";
import * as invariant from "invariant";

const OBJECTS_PER_PAGE = 20;

export const CollectionOverview: React.FunctionComponent<RouteComponentProps<{
  collectionUri: string;
  institutionUri: string;
}>> = ({match}) => {
  const collectionUri = decodeURIComponent(match.params.collectionUri);
  const institutionUri = decodeURIComponent(match.params.institutionUri);

  const initialQuery: ObjectQuery = {
    filters: {
      collectionUris: { include: [collectionUri] }
    }
  };

  const [state, setState] = useState<{
    currentObjectsPage: number;
    loadingQuery: ObjectQuery | null;
    renderedQuery: ObjectQuery | null;
    renderedObjects: ObjectSummary[] | null;
  }>({
    currentObjectsPage: 0,
    loadingQuery: initialQuery,
    renderedObjects: null,
    renderedQuery: null
  });

  const {data: initialData} = useQuery<
    CollectionOverviewInitialQuery,
    CollectionOverviewInitialQueryVariables
  >(CollectionOverviewInitialQueryDocument, {
    variables: {
      collectionUri,
      institutionUri,
      limit: OBJECTS_PER_PAGE
    },
  });

  if (!initialData) {
    return <ReactLoader loaded={false} />;
  } else if (!state.renderedObjects) {
    invariant(!state.renderedQuery, "rendered objects and query must be in sync");
    return <ReactLoader loaded={false} />;
  }

  if (!state.renderedObjects || !state.renderedQuery) {
    throw new EvalError("rendered objects and query must be set here");
  }

  const [
    refinementQuery,
    {data: refinementData},
  ] = useLazyQuery<
    CollectionOverviewRefinementQuery,
    CollectionOverviewRefinementQueryVariables
  >(CollectionOverviewRefinementQueryDocument);

  const onObjectsLoaded = (
    objects: CollectionOverviewInitialQuery_collectionByUri_objects[]
  ) => {
    setState(prevState => {
      const newState = Object.assign({}, prevState);
      if (!prevState.loadingQuery) {
        throw new EvalError();
      }
      newState.loadingQuery = null;
      newState.renderedQuery = prevState.loadingQuery;
      newState.renderedObjects = objects.map(object_ => ({
        collectionUri,
        institutionUri,
        ...object_,
      }));
      return newState;
    });
  }

  if (refinementData) {
    console.info("setting objects from more objects data");
    onObjectsLoaded(refinementData.collectionByUri.objects);
  } else if (initialData) {
    console.info("setting objects from initial data");
    onObjectsLoaded(initialData.collectionByUri.objects);
  }

  const onObjectsPageRequest = (page: number) => {
    console.info("request page " + page);
    const renderedQuery = state.renderedQuery!;
    setState(prevState =>
      Object.assign({}, prevState, {currentObjectsPage: page, loadingQuery: renderedQuery})
    );
    refinementQuery({
      variables: {collectionUri, limit: OBJECTS_PER_PAGE, offset: page * OBJECTS_PER_PAGE, query: renderedQuery},
    });
  };

  const onChangeQuery = (newQuery: ObjectQuery) => {
    if (state.loadingQuery) {
      console.warn("already loading a new query");
      return;
    }
    console.info("rendered query: " + JSON.stringify(state.renderedQuery));
    console.info("new query: " + JSON.stringify(newQuery));
    setState(prevState =>
      Object.assign({}, prevState, {loadingQuery: newQuery})
    );
    // Start over at the first page.
    refinementQuery({
      variables: {collectionUri, limit: OBJECTS_PER_PAGE, offset: 0, query: newQuery}
    })
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
        {rights ? (
          <Row className="pb-4">
            <Col xs="10">
              <RightsTable rights={rights} />
            </Col>
          </Row>
        ) : null}
        <Row>
          <Col xs={10}>
            <ObjectsGallery
              currentPage={state.currentObjectsPage}
              maxPage={Math.ceil(initialData.collectionByUri.objectsCount / OBJECTS_PER_PAGE)}
              objects={state.renderedObjects}
              onPageRequest={onObjectsPageRequest}
            />
          </Col>
          <Col className="border-left border-top" xs={2}>
            <ObjectFacets
              facets={initialData.collectionByUri.objectFacets}
              onChange={onChangeQuery}
              query={state.renderedQuery}
            />
          </Col>
        </Row>
      </Container>
    </InstitutionCollectionObjectOverview>
  );
};
