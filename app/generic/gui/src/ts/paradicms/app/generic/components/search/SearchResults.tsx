import { RouteComponentProps } from "react-router";
import * as React from "react";
import * as SearchResultsInitialQueryDocument
  from "paradicms/app/generic/api/queries/SearchResultsInitialQuery.graphql";
import { useQuery } from "@apollo/react-hooks";
import * as ReactLoader from "react-loader";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as queryString from "query-string";
import {
  SearchResultsInitialQuery,
  SearchResultsInitialQueryVariables
} from "paradicms/app/generic/api/queries/types/SearchResultsInitialQuery";

const OBJECTS_PER_PAGE = 10;

export const SearchResults: React.FunctionComponent<RouteComponentProps> = ({location}) => {
  const initialObjectQuery: ObjectQuery = queryString.parse(location.search);

  // const [state, setState] = useState<SearchResultsState>(initialSearchResultsState(initialObjectQuery));

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

  if (initialError) {
    return <GenericErrorHandler exception={new ApolloException(initialError)}/>;
  } else if (!initialData) {
    return <ReactLoader loaded={false} />;
  }

  // const [
  //   refinementQuery,
  //   {data: refinementData},
  // ] = useLazyQuery<
  //   CollectionOverviewRefinementQuery,
  //   CollectionOverviewRefinementQueryVariables
  //   >(CollectionOverviewRefinementQueryDocument);
  
  // const onObjectsLoaded = (
  //   objects: SearchResultsObjectsFragment
  // ) => {
  //   const collectionsByUri: {[index: string]: SearchResultsInitialQuery_objects_collections} = {};
  //   objects.collections.forEach(objectCollection => collectionsByUri[objectCollection.uri] = objectCollection);
  //   const institutionsByUri: {[index: string]: SearchResultsInitialQuery_objects_institutions} = {};
  //   objects.institutions.forEach(objectInstitution => institutionsByUri[objectInstitution.uri] = objectInstitution);
  //   setState(prevState => {
  //     if (!prevState.loading) {
  //       throw new EvalError();
  //     }
  //     const newState = _.cloneDeep(prevState);
  //     newState.loading = null;
  //     newState.rendered = {
  //       objects: objects.objectsWithContext.map(objectWithContext => {
  //         const collection = collectionsByUri[objectWithContext.collectionUri]!;
  //         const institution = institutionsByUri[objectWithContext.institutionUri]!;
  //         const { rights: objectRights, ...otherObjectProps } = objectWithContext.object;
  //         const rights = objectRights
  //           ? objectRights
  //           : collection.rights
  //             ? collection.rights
  //             : institution.rights;
  //         return {
  //           collectionName: collection.name,
  //           collectionUri: collection.uri,
  //           institutionName: institution.name,
  //           institutionUri: institution.uri,
  //           rights: rights ? rights.text : undefined,
  //           ...otherObjectProps,
  //         };
  //       }),
  //       objectQuery: prevState.loading?.objectQuery,
  //       objectsPage: prevState.loading.objectsPage
  //     };
  //     return newState;
  //   });
  //     // Object.assign({}, prevState, {
  //     //   // maxPage: Math.ceil(objectsCount / 10),
  //     //   objects: ,
  //     // }));
  // };

  // if (refinementData) {
  //   onObjectsLoaded(refinementData.collectionByUri.objects);
  // if (initialData) {
  //   onObjectsLoaded(initialData.objects);
  // }
  
  // const onObjectsPageRequest = (page: number) => {
  //   const renderedQuery = state.renderedQuery!;
  //   setState(prevState => ({ ...prevState, renderedPage: page, loadingQuery: renderedQuery }));
  //   //   refetch({limit: 10, offset: page * 10, query});
  //   // };
  // };
  //
  // const searchText = state.renderedQuery.text;
  //
  // return (
  //   <Frame
  //     breadcrumbItems={
  //       <React.Fragment>
  //         <BreadcrumbItem>
  //           <Link to={Hrefs.home}>Home</Link>
  //         </BreadcrumbItem>
  //         <BreadcrumbItem>
  //           <Link to={Hrefs.search(state.renderedQuery)}>
  //             Search: <i>{searchText}</i>
  //           </Link>
  //         </BreadcrumbItem>
  //       </React.Fragment>
  //     }
  //     cardTitle={
  //       <React.Fragment>
  //         Search: <i>{searchText}</i>
  //       </React.Fragment>
  //     }
  //     documentTitle={"Search results: " + searchText}
  //   >
  //     <Container fluid>
  //       <Row>
  //         <Col xs="10">
  //           <ObjectsGallery
  //             objects={state.renderedObjects}
  //             currentPage={state.renderedPage}
  //             maxPage={Math.ceil(initialData.objectsCount / OBJECTS_PER_PAGE)}
  //             onPageRequest={onObjectsPageRequest}
  //           />
  //         </Col>
  //         <Col>
  //           <ObjectFacets
  //             facets={initialData.objectFacets.facets}
  //             onChange={(newQuery) => { return; }}
  //             query={state.renderedQuery}
  //           />
  //         </Col>
  //       </Row>
  //     </Container>
  //   </Frame>
  // );
  return <div></div>;
};
