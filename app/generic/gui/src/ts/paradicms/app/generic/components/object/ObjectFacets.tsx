import * as React from "react";
import { Container, Form, FormGroup, Input, Label, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectFilters, ObjectQuery, StringFacetFilter } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as invariant from "invariant";
import * as _ from "lodash";
import { ObjectFacetsFragment } from "paradicms/app/generic/api/queries/types/ObjectFacetsFragment";

const StringFacetFilterListGroup: React.FunctionComponent<{
  allValues: string[];
  currentState?: StringFacetFilter;
  onChange: (newState?: StringFacetFilter) => void;
  title: string;
}> = ({allValues, currentState, onChange, title}) => {
  if (allValues.length === 0) {
    return null;
  }

  // Build sets of the exclude and include values to avoid repeatedly iterating over the arrays.
  const toSet = (values: string[] | null | undefined): {[index: string]: true} => {
    const result: {[index: string]: true} = {};
    for (const value of (values ? values : [])) {
      result[value] = true;
    }
    return result;
  }
  toSet(allValues); // To check for duplicates
  const excludeSet = toSet(currentState ? currentState.exclude : undefined);
  const includeSet = toSet(currentState ? currentState.include : undefined);

  // If a value is not in one of the sets it's implicitly included.
  for (const value of allValues) {
    if (value in excludeSet) {
      invariant(!(value in includeSet), "value both included and excluded");
    } else if (value in includeSet) {
      continue;
    } else {
      includeSet[value] = true;
    }
  }
  invariant(Object.keys(includeSet).length + Object.keys(excludeSet).length === allValues.length, "sets should account for all values");

  return (
    <React.Fragment>
      <Row>
        <h4 className="text-center w-100">{title}</h4>
      </Row>
      <Row>
        <ListGroup className="w-100">
          {allValues.map(value => {
            const onChangeValue = (e: React.ChangeEvent<HTMLInputElement>): void => {
              const newChecked = e.target.checked;
              if (newChecked) {
                invariant(value in excludeSet, "value should have been in the exclude set if it wasn't checked before");
                delete excludeSet[value];
                includeSet[value] = true;
              } else {
                invariant(value in includeSet, "value should have been in the include set if it was checked before");
                delete includeSet[value];
                excludeSet[value] = true;
              }

              const exclude = Object.keys(excludeSet).sort();
              const include = Object.keys(includeSet).sort();
              invariant(include.length + exclude.length === allValues.length, "sets should account for all values");

              if (include.length === allValues.length) {
                onChange(undefined); // Implicitly include all values
              } else if (include.length >= exclude.length) {
                // exclude includes fewer values. Those outside it will be included.
                onChange({exclude});
              } else {
                // include includes fewer values. Those outside it will be excluded.
                onChange({include});
              }
            };

            return (
              <ListGroupItem className="w-100" key={value}>
                <FormGroup check>
                  <Label check>
                    <Input checked={!!includeSet[value]} onChange={onChangeValue} type="checkbox"></Input>
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
  facets: ObjectFacetsFragment;
  onChange: (query: ObjectQuery) => void;
  query: ObjectQuery;
}> = ({facets, onChange, query}) => {
  const isFiltersEmpty = (filters: ObjectFilters): boolean => {
    return !filters.collectionUris && !filters.institutionUris && !filters.subjects && !filters.types;
  }

  const onChangeStringFacetFilter = (attribute: keyof ObjectFilters, newState?: StringFacetFilter) => {
    const newQuery: ObjectQuery = _.cloneDeep(query);
    if (!newQuery.filters) {
      newQuery.filters = {};
    }
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

