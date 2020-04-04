import { CollectionOverviewQuery_collectionByUri_objects_facets } from "paradicms/app/generic/api/queries/types/CollectionOverviewQuery";
import * as React from "react";
import { Container, ListGroup, ListGroupItem, Row } from "reactstrap";

export const ObjectFacets: React.FunctionComponent<{ objectFacets: CollectionOverviewQuery_collectionByUri_objects_facets}> = ({objectFacets}) => (
  <Container fluid>
    {objectFacets.subjects.length > 0 ?
      <React.Fragment>
        <Row className="text-center">
          <h2>Subjects</h2>
        </Row>
        <Row>
          <ListGroup>
            {objectFacets.subjects.map(subject => <ListGroupItem key={subject}>{subject})</ListGroupItem>)}
          </ListGroup>
        </Row>
      </React.Fragment>
      : null}
  </Container>
);

