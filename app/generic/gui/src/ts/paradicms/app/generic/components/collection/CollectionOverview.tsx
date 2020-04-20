import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import * as CollectionOverviewInitialQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewInitialQuery.graphql";
import {
  CollectionOverviewInitialQuery,
  CollectionOverviewInitialQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewInitialQuery";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import * as CollectionOverviewRefinementQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewRefinementQuery.graphql";
import { useLazyQuery, useQuery } from "@apollo/react-hooks";
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
import * as invariant from "invariant";

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
  // console.debug("State: " + JSON.stringify(state));

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

  // This isn't used until later, but all hooks must be rendered on every call to render().
  const [
    refinementQuery,
    {data: refinementData},
  ] = useLazyQuery<
    CollectionOverviewRefinementQuery,
    CollectionOverviewRefinementQueryVariables
    >(CollectionOverviewRefinementQueryDocument);

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    return <ReactLoader loaded={false} />;
  }

  if (state.loading) {
    let newData: CollectionOverviewRefinementQuery_collectionByUri | undefined;
    if (refinementData) {
      newData = refinementData.collectionByUri;
    } else if (!state.rendered) {
      newData = initialData.collectionByUri;
    }

    if (newData) {
      // Having this as a separate function creates a stale closure of an old setState.
      setState(prevState => {
        if (!prevState.loading) {
          throw new EvalError();
        }
        return {
          loading: null,
          rendered: {
            objects: newData!.objects.map(object_ => ({
              collectionUri,
              institutionUri,
              ...object_,
            })),
            objectsCount: newData!.objectsCount,
            objectQuery: prevState.loading.objectQuery,
            objectsPage: prevState.loading.objectsPage
          }
        };
      });
    }
  }

  if (!state.rendered) {
    return <ReactLoader loaded={false}/>;
  }

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
      variables: {collectionUri, limit: OBJECTS_PER_PAGE, offset: page * OBJECTS_PER_PAGE, query: state.rendered!.objectQuery},
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
              currentPage={state.rendered.objectsPage}
              maxPage={Math.ceil(state.rendered.objectsCount / OBJECTS_PER_PAGE)}
              objects={state.rendered.objects}
              onPageRequest={onObjectsPageRequest}
            />
          </Col>
          <Col className="border-left border-top" xs={2}>
            <ObjectFacets
              facets={initialData.collectionByUri.objectFacets}
              onChange={onChangeObjectQuery}
              query={state.rendered.objectQuery}
            />
          </Col>
        </Row>
      </Container>
    </InstitutionCollectionObjectOverview>
  );
};
