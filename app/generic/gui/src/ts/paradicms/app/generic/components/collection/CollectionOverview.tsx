import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import * as CollectionOverviewQueryDocument from "paradicms/app/generic/api/queries/CollectionOverviewQuery.graphql";
import {
  CollectionOverviewQuery,
  CollectionOverviewQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewQuery";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import {
  CollectionOverviewObjectsPaginationQuery,
  CollectionOverviewObjectsPaginationQuery_collectionByUri_objects,
  CollectionOverviewObjectsPaginationQueryVariables
} from "paradicms/app/generic/api/queries/types/CollectionOverviewObjectsPaginationQuery";
import * as CollectionOverviewObjectsPaginationQueryDocument
  from "paradicms/app/generic/api/queries/CollectionOverviewObjectsPaginationQuery.graphql";
import { useLazyQuery, useQuery } from "@apollo/react-hooks";
import { ObjectSummary } from "paradicms/app/generic/components/object/ObjectSummary";
import * as ReactLoader from "react-loader";
import { InstitutionCollectionObjectOverview } from "paradicms/app/generic/components/frame/InstitutionCollectionObjectOverview";
import { RightsTable } from "paradicms/app/generic/components/rights/RightsTable";
import { Col, Container, Row } from "reactstrap";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";

export const CollectionOverview: React.FunctionComponent<RouteComponentProps<{
  collectionUri: string;
  institutionUri: string;
}>> = ({match}) => {
  const collectionUri = decodeURIComponent(match.params.collectionUri);
  const institutionUri = decodeURIComponent(match.params.institutionUri);

  const [state, setState] = useState<{
    currentObjectsPage: number;
    objects: ObjectSummary[] | null;
  }>({
    currentObjectsPage: 0,
    objects: null,
  });

  const setObjects = (
    objects: CollectionOverviewObjectsPaginationQuery_collectionByUri_objects
  ) => {
    setState(prevState =>
      Object.assign({}, prevState, {
        objects: objects.objects.map(object_ => ({
          collectionUri,
          institutionUri,
          ...object_,
        })),
      })
    );
  };

  const {loading: initialLoading, data: initialData} = useQuery<
    CollectionOverviewQuery,
    CollectionOverviewQueryVariables
  >(CollectionOverviewQueryDocument, {
    variables: {
      collectionUri,
      institutionUri,
    },
  });

  const [
    getMoreObjects,
    {loading: moreObjectsLoading, data: moreObjectsData},
  ] = useLazyQuery<
    CollectionOverviewObjectsPaginationQuery,
    CollectionOverviewObjectsPaginationQueryVariables
  >(CollectionOverviewObjectsPaginationQueryDocument);

  if (initialLoading || moreObjectsLoading) {
    return <ReactLoader loaded={false} />;
  } else if (state.objects === null) {
    if (moreObjectsData) {
      console.info("setting objects from more objects data");
      setObjects(moreObjectsData.collectionByUri.objects);
    } else if (initialData) {
      console.info("setting objects from initial data");
      setObjects(initialData.collectionByUri.objects);
    }
    return <ReactLoader loaded={false} />;
  }

  const onObjectsPageRequest = (page: number) => {
    console.info("request page " + page);
    setState(prevState =>
      Object.assign({}, prevState, {currentObjectsPage: page, objects: null})
    );
    getMoreObjects({
      variables: {collectionUri, limit: 20, offset: page * 20},
    });
  };

  const rights = initialData
    ? initialData.collectionByUri.rights
      ? initialData.collectionByUri.rights
      : initialData.institutionByUri.rights
    : undefined;

  return (
    <InstitutionCollectionObjectOverview
      collectionName={initialData!.collectionByUri.name}
      collectionUri={collectionUri}
      institutionName={initialData!.institutionByUri.name}
      institutionUri={institutionUri}
      title={initialData!.collectionByUri.name}
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
              maxPage={Math.ceil(initialData!.collectionByUri.objectsCount / 20)}
              objects={state.objects}
              onPageRequest={onObjectsPageRequest}
            />
          </Col>
          <Col className="border-left border-top" xs={2}>
            <ObjectFacets
              facets={initialData!.collectionByUri.objects.facets}
              onChange={(query) => { return; }}
              query={{collectionUri}}
            />
          </Col>
        </Row>
      </Container>
    </InstitutionCollectionObjectOverview>
  );
};
