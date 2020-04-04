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
import { BreadcrumbItem } from "reactstrap";
import { Link } from "react-router-dom";
import { Hrefs } from "paradicms/app/generic/Hrefs";

export const SearchResults: React.FunctionComponent<RouteComponentProps<{
  text: string;
}>> = ({match}) => {
  const searchText = decodeURIComponent(match.params.text);

  const [state, setState] = useState<{
    currentPage: number;
    maxPage: number;
    objects: ObjectSummary[] | null;
  }>({
    currentPage: 0,
    maxPage: 0,
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

  const {loading, data, refetch} = useQuery<
    SearchResultsQuery,
    SearchResultsQueryVariables
  >(SearchResultsQueryDocument, {
    variables: {
      limit: 10,
      offset: 0,
      text: searchText,
    },
  });

  if (loading) {
    return <ReactLoader loaded={false} />;
  }

  if (state.objects == null) {
    setObjects(data!.objects, data!.objectsCount);
  }

  const onPageRequest = (page: number) => {
    setState(prevState => ({...prevState, currentPage: page, objects: null}));
    refetch({limit: 10, offset: page * 10, text: searchText});
  };

  return (
    <Frame
      breadcrumbItems={
        <React.Fragment>
          <BreadcrumbItem>
            <Link to={Hrefs.home}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem>
            <Link to={Hrefs.search(searchText)}>
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
      <ObjectsGallery
        objects={state.objects!}
        currentPage={state.currentPage}
        maxPage={state.maxPage!}
        onPageRequest={onPageRequest}
      />
    </Frame>
  );
};
