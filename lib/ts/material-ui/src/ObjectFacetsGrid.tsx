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
  PropertyFilter,
  StringFilter,
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
  const onChangePropertyFilter = (
    propertyDefinitionUri: string,
    newState?: StringFilter
  ) => {
    const {properties: oldProperties, ...oldFiltersWithoutProperties} = filters;

    let newPropertyFilters: PropertyFilter[];
    if (!oldProperties) {
      newPropertyFilters = [];
    } else {
      // Remove the key's filter and then add it back
      newPropertyFilters = oldProperties.filter(
        propertyFilter =>
          propertyFilter.propertyDefinitionUri !== propertyDefinitionUri
      );
    }
    if (newState) {
      newPropertyFilters.push({propertyDefinitionUri, ...newState});
    }

    let newFilters: ObjectFilters;
    if (newPropertyFilters.length === 0) {
      newFilters = oldFiltersWithoutProperties;
    } else {
      newFilters = {
        ...oldFiltersWithoutProperties,
        properties: newPropertyFilters,
      };
    }

    onChange(newFilters);
  };

  return (
    <Grid container direction="column" spacing={4}>
      {(facets.properties ?? []).map(propertyFacet => (
        <Grid item key={propertyFacet.definition.uri}>
          <FacetExpansionPanel
            id={propertyFacet.definition.uri}
            title={propertyFacet.definition.labelSingular}
          >
            <StringFacetForm
              valueUniverse={propertyFacet.values}
              currentState={filters.properties?.find(
                propertyFilter =>
                  propertyFilter.propertyDefinitionUri ===
                  propertyFacet.definition.uri
              )}
              onChange={newState =>
                onChangePropertyFilter(propertyFacet.definition.uri, newState)
              }
            />
          </FacetExpansionPanel>
        </Grid>
      ))}
    </Grid>
  );
};
