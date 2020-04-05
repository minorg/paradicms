import { RouteComponentProps } from "react-router";
import * as React from "react";
import { InstitutionCollectionObjectOverview } from "paradicms/app/generic/components/frame/InstitutionCollectionObjectOverview";
import { useQuery } from "@apollo/react-hooks";
import {
  ObjectOverviewQuery,
  ObjectOverviewQueryVariables
} from "paradicms/app/generic/api/queries/types/ObjectOverviewQuery";
import * as ObjectOverviewQueryDocument from "paradicms/app/generic/api/queries/ObjectOverviewQuery.graphql";
import { Card, CardBody, CardHeader, CardTitle, Container, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectImagesCarousel } from "paradicms/app/generic/components/object/ObjectImagesCarousel";
import { RightsTable } from "paradicms/app/generic/components/rights/RightsTable";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import * as ReactLoader from "react-loader";

// type Object = ObjectCardObject;

export const ObjectOverview: React.FunctionComponent<RouteComponentProps<{
  collectionUri: string;
  institutionUri: string;
  objectUri: string;
}>> = ({ match }) => {
  const collectionUri = decodeURIComponent(match.params.collectionUri);
  const institutionUri = decodeURIComponent(match.params.institutionUri);
  const objectUri = decodeURIComponent(match.params.objectUri);

  const { data, error, loading } = useQuery<ObjectOverviewQuery, ObjectOverviewQueryVariables>(ObjectOverviewQueryDocument, {
    variables: {
      collectionUri, institutionUri, objectUri
    }
  });

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false}/>;
  } else if (!data) {
    throw new EvalError();
  }

  const nameValueTableRows = (name: string, values: string[]) =>
    values.map(value => (
      <tr>
        <td className="px-2">
          <strong>{name}</strong>
        </td>
        <td className="px-2">{value}</td>
      </tr>
    ));

  const listGroupSection = (title: string, values: string[]) =>
    values.length > 0 ? (
      <Row className="pb-4">
        <Card className="w-100">
          <CardHeader>
            <CardTitle>
              <h5>{title}</h5>
            </CardTitle>
          </CardHeader>
          <CardBody>
            <ListGroup>
              {values.map(value => (
                <ListGroupItem key={value}>{value}</ListGroupItem>
              ))}
            </ListGroup>
          </CardBody>
        </Card>
      </Row>
    ) : null;

  const object_ = data.objectByUri;
  const rights = data.objectByUri.rights
    ? data.objectByUri.rights
    : data.collectionByUri.rights
      ? data.collectionByUri.rights
      : data.institutionByUri.rights;
  return (
    <InstitutionCollectionObjectOverview
      collectionName={data.collectionByUri.name}
      collectionUri={collectionUri}
      institutionName={data.institutionByUri.name}
      institutionUri={institutionUri}
      objectTitle={data.objectByUri.title}
      objectUri={objectUri}
      title={data.objectByUri.title}
    >
      <Container fluid>
        {object_.images.length > 0 ? (
          <Row className="pb-4">
            <Card className="w-100">
              <CardBody>
                <ObjectImagesCarousel images={object_.images}/>
              </CardBody>
            </Card>
          </Row>
        ) : null}
        {listGroupSection("Descriptions", object_.descriptions)}
        {object_.titles.length > 1 ||
        object_.alternativeTitles.length > 0 ||
        object_.titles[0] !== object_.title ? (
          <Row className="pb-4">
            <Card className="w-100">
              <CardHeader>
                <CardTitle>
                  <h5>Titles</h5>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <table className="table-bordered w-100">
                  <tbody>
                  {nameValueTableRows("Title", object_.titles)}
                  {nameValueTableRows(
                    "Alternative title",
                    object_.alternativeTitles
                  )}
                  </tbody>
                </table>
              </CardBody>
            </Card>
          </Row>
        ) : null}
        {listGroupSection("Identifiers", object_.identifiers)}
        {listGroupSection("Subjects", object_.subjects)}
        {object_.creators.length > 0 ||
        object_.provenances.length > 0 ||
        object_.publishers.length > 0 ||
        object_.sources.length > 0 ? (
          <Row className="pb-4">
            <Card className="w-100">
              <CardHeader>
                <CardTitle>
                  <h5>Provenance</h5>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <table className="table-bordered w-100">
                  <tbody>
                  {nameValueTableRows("Creator", object_.creators)}
                  {nameValueTableRows("Publisher", object_.publishers)}
                  {nameValueTableRows(
                    "Provenance",
                    object_.provenances
                  )}
                  {nameValueTableRows("Source", object_.sources)}
                  </tbody>
                </table>
              </CardBody>
            </Card>
          </Row>
        ) : null}
        {object_.dates.length > 0 ||
        object_.extents.length > 0 ||
        object_.languages.length > 0 ||
        object_.media.length > 0 ||
        object_.spatialCoverages.length > 0 ? (
          <Row className="pb-4">
            <Card className="w-100">
              <CardHeader>
                <CardTitle>
                  <h5>Extent</h5>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <table className="table-bordered w-100">
                  <tbody>
                  {nameValueTableRows("Date", object_.dates)}
                  {nameValueTableRows("Extent", object_.extents)}
                  {nameValueTableRows("Language", object_.languages)}
                  {nameValueTableRows("Medium", object_.media)}
                  {nameValueTableRows(
                    "Spatial coverage",
                    object_.spatialCoverages
                  )}
                  </tbody>
                </table>
              </CardBody>
            </Card>
          </Row>
        ) : null}
        {rights ? (
          <Row className="pb-4">
            <Card className="w-100">
              <CardHeader>
                <CardTitle>
                  <h5>Rights</h5>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <RightsTable className="table-bordered" rights={rights}/>
              </CardBody>
            </Card>
          </Row>
        ) : null}
      </Container>
    </InstitutionCollectionObjectOverview>
  );
};
