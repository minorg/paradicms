import { RouteComponentProps } from "react-router";
import * as React from "react";
import { useState } from "react";
import * as SearchResultsQueryDocument from "paradicms/app/generic/api/queries/SearchResultsQuery.graphql";
import {
  SearchResultsQuery,
  SearchResultsQuery_objects,
  SearchResultsQuery_objects_collections,
  SearchResultsQuery_objects_institutions,
  SearchResultsQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsQuery";
import { ObjectsGallery } from "paradicms/app/generic/components/object/ObjectsGallery";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import { ObjectSummary } from "paradicms/app/generic/components/object/ObjectSummary";
import { useQuery } from "@apollo/react-hooks";
import * as ReactLoader from "react-loader";
import { BreadcrumbItem, Col, Container, Row } from "reactstrap";
import { Link } from "react-router-dom";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "paradicms-base";
import { ObjectsQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as queryString from "query-string";
import { ObjectFacets } from "paradicms/app/generic/components/object/ObjectFacets";
import { CollectionOverviewQuery_collectionByUri_objects_facets } from "paradicms/app/generic/api/queries/types/CollectionOverviewQuery";

export const SearchResults: React.FunctionComponent<RouteComponentProps> = ({location}) => {
  const query: ObjectsQuery = queryString.parse(location.search);

  const [state, setState] = useState<{
    currentPage: number;
    objectFacets: CollectionOverviewQuery_collectionByUri_objects_facets | null;
    maxPage: number;
    objects: ObjectSummary[] | null;
  }>({
    currentPage: 0,
    maxPage: 0,
    objectFacets: null,
    objects: null,
  });
  console.info("State is ", JSON.stringify(state));

  const setObjects = (
    objects: SearchResultsQuery_objects,
    objectsCount: number
  ) => {
    const collectionsByUri: {[index: string]: SearchResultsQuery_objects_collections} = {};
    objects.collections.forEach(objectCollection => collectionsByUri[objectCollection.uri] = objectCollection);
    const institutionsByUri: {[index: string]: SearchResultsQuery_objects_institutions} = {};
    objects.institutions.forEach(objectInstitution => institutionsByUri[objectInstitution.uri] = objectInstitution);
    setState(prevState =>
      Object.assign({}, prevState, {
        maxPage: Math.ceil(objectsCount / 10),
        objectFacets: objects.facets,
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

  const {loading, data, error, refetch} = useQuery<
    SearchResultsQuery,
    SearchResultsQueryVariables
  >(SearchResultsQueryDocument, {
    variables: {
      limit: 10,
      offset: 0,
      query
    },
  });

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false} />;
  }

  if (state.objects == null) {
    setObjects(data!.objects, data!.objectsCount);
  }

  const onPageRequest = (page: number) => {
    setState(prevState => ({...prevState, currentPage: page, objects: null}));
    refetch({limit: 10, offset: page * 10, query});
  };

  const searchText = query.text;

  return (
    <Frame
      breadcrumbItems={
        <React.Fragment>
          <BreadcrumbItem>
            <Link to={Hrefs.home}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem>
            <Link to={Hrefs.search(query)}>
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
              objects={state.objects!}
              currentPage={state.currentPage}
              maxPage={state.maxPage}
              onPageRequest={onPageRequest}
            />
          </Col>
          <Col>
            <ObjectFacets facets={state.objectFacets!} query={query}/>
          </Col>
        </Row>
      </Container>
    </Frame>
  );
};
