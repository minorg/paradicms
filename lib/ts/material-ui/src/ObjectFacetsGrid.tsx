import * as React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
} from "@material-ui/core";
import {
  ObjectFacets,
  ObjectFilters,
  ObjectFiltersState,
} from "@paradicms/models";
import {StringFacetForm} from "./StringFacetForm";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";

const FacetExpansionPanel: React.FunctionComponent<React.PropsWithChildren<{
  id: string;
  title: string;
}>> = ({children, id, title}) => {
  return (
    <Grid item className="facet" data-cy={id + "-facet"}>
      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          {title}
        </AccordionSummary>
        <AccordionDetails>{children}</AccordionDetails>
      </Accordion>
    </Grid>
  );
};

export const ObjectFacetsGrid: React.FunctionComponent<{
  facets: ObjectFacets;
  filters: ObjectFilters;
  onChange: (filters: ObjectFilters) => void;
}> = ({facets, filters, onChange}) => {
  const filtersState = new ObjectFiltersState(filters);

  return (
    <Grid container direction="column" spacing={4}>
      {(facets.properties ?? []).map(propertyFacet => (
        <Grid item key={propertyFacet.definition.uri}>
          <FacetExpansionPanel
            id={propertyFacet.definition.uri}
            title={propertyFacet.definition.label}
          >
            <StringFacetForm
              valueUniverse={propertyFacet.values}
              currentState={filtersState.getPropertyFilter(
                propertyFacet.definition.uri
              )}
              onChange={newState => {
                if (newState) {
                  filtersState.setPropertyFilter({
                    propertyDefinitionUri: propertyFacet.definition.uri,
                    ...newState,
                  });
                } else {
                  filtersState.removePropertyFilter(
                    propertyFacet.definition.uri
                  );
                }
                onChange(filtersState.snapshot);
              }}
            />
          </FacetExpansionPanel>
        </Grid>
      ))}
    </Grid>
  );
};
