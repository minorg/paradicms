import * as React from "react";
import { Container, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectsQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";

const StringObjectFacet: React.FunctionComponent<{ title: string, values: string[] }> = ({title, values}) => (
  values.length > 0 ?
    <React.Fragment>
      <Row>
        <h4 className="text-center w-100">{title}</h4>
      </Row>
      <Row>
        <ListGroup className="w-100">
          {values.map(value => <ListGroupItem className="w-100" key={value}>{value}</ListGroupItem>)}
        </ListGroup>
      </Row>
      <Row>&nbsp;</Row>
    </React.Fragment>
    : null);

export const ObjectFacets: React.FunctionComponent<{
  facets: {
    subjects: string[];
    types: string[];
  };
  query: ObjectsQuery;
}> = ({facets, query}) => (
  <Container className="py-4" fluid>
    <StringObjectFacet title={"Subjects"} values={facets.subjects}/>
    <StringObjectFacet title={"Types"} values={facets.types}/>
  </Container>
);

