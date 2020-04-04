import * as React from "react";
import { Container, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectsQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";

export const ObjectFacets: React.FunctionComponent<{
  facets: {
    subjects: string[];
  };
  onChange: (newQuery: ObjectsQuery) => void;
  query: ObjectsQuery;
}> = ({facets, query}) => (
  <Container className="py-4" fluid>
    {facets.subjects.length > 0 ?
      <React.Fragment>
        <Row className="text-center">
          <h4>Subjects</h4>
        </Row>
        <Row>
          <ListGroup>
            {facets.subjects.map(subject => <ListGroupItem key={subject}>{subject})</ListGroupItem>)}
          </ListGroup>
        </Row>
      </React.Fragment>
      : null}
  </Container>
);

