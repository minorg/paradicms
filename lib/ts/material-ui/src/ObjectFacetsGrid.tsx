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

export const ObjectFacetsGrid: React.FunctionComponent<{
  facets: ObjectFacets;
  filters: ObjectFilters;
  onChange: (filters: ObjectFilters) => void;
}> = ({facets, filters, onChange}) => {
  const filtersState = new ObjectFiltersState(filters);

  return (
    <Grid container direction="column" spacing={4}>
      {(facets.properties ?? []).map(propertyFacet => (
        <Grid
          className="facet"
          data-cy={propertyFacet.definition.uri + "-facet"}
          item
          key={propertyFacet.definition.uri}
        >
          <Accordion>
            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
              {propertyFacet.definition.label}
            </AccordionSummary>
            <AccordionDetails>
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
            </AccordionDetails>
          </Accordion>{" "}
        </Grid>
      ))}
    </Grid>
  );
};
