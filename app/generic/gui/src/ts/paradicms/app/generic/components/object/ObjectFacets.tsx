import * as React from "react";
import { Container, Form, FormGroup, Input, Label, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectQuery } from "paradicms/app/generic/api/graphqlGlobalTypes";

const StringObjectFacet: React.FunctionComponent<{
  allValues: string[];
  excludeValues: string[];
  onChange: (kwds: {excludeValues: string[], includeValues: string[]}) => void;
  includeValues: string[];
  title: string;
}> = ({allValues, excludeValues, includeValues, onChange, title}) => (
  allValues.length > 0 ?
    <React.Fragment>
      <Row>
        <h4 className="text-center w-100">{title}</h4>
      </Row>
      <Row>
        <ListGroup className="w-100">
          {allValues.map(value => {
            let checked: boolean;
            let excluded: boolean = false;
            let included: boolean = false;
            if (includeValues.length === 0 && excludeValues.length === 0) {
              checked = true;
            } else if (includeValues.find(includeValue => includeValue === value)) {
              included = true;
              checked = true;
            } else if (excludeValues.find(excludeValue => excludeValue === value)) {
              excluded = true;
              checked = false;
            } else {
              console.warn(title + " value " + value + " is neither included nor excluded");
              return null;
            }

            const onChangeValue = (e: React.ChangeEvent<HTMLInputElement>): void => {
              const newChecked = e.target.checked;
              let newExcludeValues = excludeValues.concat();
              let newIncludeValues = includeValues.concat();
              if (excluded) {
                newExcludeValues = newExcludeValues.filter(excludedValue => excludedValue !== value);
                newIncludeValues.push(value);
              } else if (included) {
                newIncludeValues = newIncludeValues.filter(includedValue => includedValue !== value);
                newExcludeValues.push(value);
              } else if (newChecked) {
                newIncludeValues.push(value);
              } else {
                newExcludeValues.push(value);
              }
              console.debug(title + ": " + value + ": checked=" + checked);
              console.debug("old exclude values: " + excludeValues.join(" | "));
              console.debug("new exclude values: " + newExcludeValues.join(" | "));
              console.debug("old include values: " + includeValues.join(" | "));
              console.debug("new include values: " + newIncludeValues.join(" | "));
              onChange({excludeValues: newExcludeValues, includeValues: newIncludeValues});
            };

            return (
              <ListGroupItem className="w-100" key={value}>
                <FormGroup check>
                  <Label check>
                    <Input checked={checked} onChange={onChangeValue} type="checkbox"></Input>
                    {value}
                  </Label>
                </FormGroup>
              </ListGroupItem>
            );
          })}
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
  onChange: (query: ObjectQuery) => void;
  query: ObjectQuery;
}> = ({facets, query}) => {
  const onChangeSubject = (kwds: {excludeValues: string[], includeValues: string[]}) => { return; }
  const onChangeType = (kwds: {excludeValues: string[], includeValues: string[]}) => { return; }

  return (
    <Form>
      <Container className="py-4" fluid>
        <StringObjectFacet allValues={facets.subjects} excludeValues={[]} includeValues={facets.subjects}
                           onChange={onChangeSubject}
                           title={"Subjects"}/>
        <StringObjectFacet allValues={facets.types} excludeValues={[]} includeValues={[]} onChange={onChangeType} title={"Types"}/>
      </Container>
    </Form>
  );
};

