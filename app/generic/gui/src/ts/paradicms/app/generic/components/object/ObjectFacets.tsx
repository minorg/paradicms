import * as React from "react";
import { Container, Form, FormGroup, Input, Label, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectFilters, ObjectQuery, StringFacetFilter } from "paradicms/app/generic/api/graphqlGlobalTypes";

const StringFacetFilterListGroup: React.FunctionComponent<{
  allValues: string[];
  currentState?: StringFacetFilter;
  onChange: (newState?: StringFacetFilter) => void;
  title: string;
}> = ({allValues, currentState, onChange, title}) => {
  if (allValues.length === 0) {
    return null;
  }

  const excludeValues = (currentState && currentState.exclude) ? currentState!.exclude : [];
  const includeValues = (currentState && currentState.include) ? currentState!.include : [];

  return (
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

              if (newExcludeValues.length === 0 && newIncludeValues.length === 0) {
                onChange(undefined);
              } else {
                onChange({
                  exclude: newExcludeValues.length > 0 ? newExcludeValues : undefined,
                  include: newIncludeValues.length > 0 ? newIncludeValues : undefined
                });
              }
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
    </React.Fragment>);
}

export const ObjectFacets: React.FunctionComponent<{
  facets: {
    subjects: string[];
    types: string[];
  };
  onChange: (query: ObjectQuery) => void;
  query: ObjectQuery;
}> = ({facets, onChange, query}) => {
  const isFiltersEmpty = (filters: ObjectFilters) => {
    return filters.collectionUris || filters.institutionUris || filters.subjects || filters.types;
  }

  const onChangeStringFacetFilter = (attribute: keyof ObjectFilters, newState?: StringFacetFilter) => {
    const newQuery = Object.assign({}, query);
    newQuery.filters = Object.assign({}, newQuery.filters);
    newQuery.filters[attribute] = newState;
    if (isFiltersEmpty(newQuery.filters)) {
      newQuery.filters = undefined;
    }
    onChange(newQuery);
  }

  const onChangeSubject = (newState?: StringFacetFilter) => onChangeStringFacetFilter("subjects", newState);
  const onChangeType = (newState?: StringFacetFilter) => onChangeStringFacetFilter("types", newState);

  return (
    <Form>
      <Container className="py-4" fluid>
        <StringFacetFilterListGroup allValues={facets.subjects}
                     currentState={query.filters && query.filters.subjects ? query.filters.subjects : undefined}
                     onChange={onChangeSubject}
                     title={"Subjects"}/>
        <StringFacetFilterListGroup allValues={facets.types}
                     currentState={query.filters && query.filters.types ? query.filters.types : undefined}
                     onChange={onChangeType}
                     title={"Types"}/>
      </Container>
    </Form>
  );
};

