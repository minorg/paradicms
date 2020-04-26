import * as React from "react";
import { useState } from "react";
import { Col, Collapse, Container, Form, FormGroup, Input, Label, ListGroup, ListGroupItem, Row } from "reactstrap";
import { ObjectFilters, ObjectQuery, StringFacetFilter } from "paradicms/app/generic/api/graphqlGlobalTypes";
import * as invariant from "invariant";
import * as _ from "lodash";
import { ObjectFacetsFragment } from "paradicms/app/generic/api/queries/types/ObjectFacetsFragment";
import * as classnames from "classnames";

const FacetDisclosurePanel: React.FunctionComponent<{children: React.ReactNode; title: string}> = ({children, title}) => {
  const [state, setState] = useState<{open: boolean}>({open: false});
  const {open} = state;
  const onToggle = () => setState(prevState => ({open: !prevState.open}));

  return (
    <React.Fragment>
    <Row>
      <Col xs={12}>
        <a onClick={onToggle} style={{cursor: "pointer", fontSize: "larger"}}>{title}</a>
        <div className="float-right">
          <a onClick={onToggle} style={{cursor: "pointer"}}>
            <i
              className={classnames({
                fas: true,
                "fa-chevron-down": open,
                "fa-chevron-right": !open,
              })}
            ></i>
          </a>
        </div>
        <Collapse isOpen={open}>
          <div className="mt-2">
            {children}
          </div>
        </Collapse>
      </Col>
    </Row>
    <Row>&nbsp;</Row>
  </React.Fragment>
  );
}

const StringFacet: React.FunctionComponent<{
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
    <FacetDisclosurePanel title={title}>
        <ListGroup className="w-100">
          {allValues.sort().map(value => {
            const onChangeValue = (e: React.ChangeEvent<HTMLInputElement>): void => {
              const newChecked = e.target.checked;
              delete excludeSet[value];
              delete includeSet[value];
              if (newChecked) {
                includeSet[value] = true;
              } else {
                excludeSet[value] = true;
              }

              const exclude = Object.keys(excludeSet).sort();
              const include = Object.keys(includeSet).sort();
              invariant(include.length + exclude.length === allValues.length, "sets should account for all values");

              if (include.length === allValues.length) {
                onChange(undefined); // Implicitly include all values
              } else if (exclude.length === allValues.length) {
                onChange({exclude}); // Explicitly exclude all values
              } else if (include.length >= exclude.length) {
                invariant(exclude.length > 0, "must explicitly exclude");
                // exclude includes fewer values. Those outside it will be included.
                onChange({exclude});
              } else {
                // include includes fewer values. Those outside it will be excluded.
                invariant(include.length > 0, "must explicitly include");
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
  </FacetDisclosurePanel>);
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

  const onChangeCulturalContext = (newState?: StringFacetFilter) => onChangeStringFacetFilter("culturalContexts", newState);
  const onChangeMaterial = (newState?: StringFacetFilter) => onChangeStringFacetFilter("materials", newState);
  const onChangeSpatial = (newState?: StringFacetFilter) => onChangeStringFacetFilter("spatials", newState);
  const onChangeSubject = (newState?: StringFacetFilter) => onChangeStringFacetFilter("subjects", newState);
  const onChangeTechnique = (newState?: StringFacetFilter) => onChangeStringFacetFilter("techniques", newState);
  const onChangeTemporal = (newState?: StringFacetFilter) => onChangeStringFacetFilter("temporals", newState);
  const onChangeType = (newState?: StringFacetFilter) => onChangeStringFacetFilter("types", newState);

  return (
    <Form>
      <Container className="py-4" fluid>
        <StringFacet allValues={facets.subjects}
                     currentState={query.filters && query.filters.subjects ? query.filters.subjects : undefined}
                     onChange={onChangeSubject}
                     title={"Subjects"}/>
        <StringFacet allValues={facets.types}
                     currentState={query.filters && query.filters.types ? query.filters.types : undefined}
                     onChange={onChangeType}
                     title={"Types"}/>
        <StringFacet allValues={facets.culturalContexts}
                                    currentState={query.filters && query.filters.culturalContexts ? query.filters.culturalContexts : undefined}
                                    onChange={onChangeCulturalContext}
                                    title={"Cultural context"}/>
        <StringFacet allValues={facets.materials}
                                    currentState={query.filters && query.filters.materials ? query.filters.materials : undefined}
                                    onChange={onChangeMaterial}
                                    title={"Material"}/>
        <StringFacet allValues={facets.spatials}
                                    currentState={query.filters && query.filters.spatials ? query.filters.spatials : undefined}
                                    onChange={onChangeSpatial}
                                    title={"Spatial coverage"}/>
        <StringFacet allValues={facets.techniques}
                                    currentState={query.filters && query.filters.techniques ? query.filters.techniques : undefined}
                                    onChange={onChangeTechnique}
                                    title={"Technique"}/>
        <StringFacet allValues={facets.temporals}
                                    currentState={query.filters && query.filters.temporals ? query.filters.temporals : undefined}
                                    onChange={onChangeTemporal}
                                    title={"Temporal coverage"}/>
      </Container>
    </Form>
  );
};

