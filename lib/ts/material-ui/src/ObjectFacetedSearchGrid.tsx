import {
  Collection,
  Image,
  Institution,
  JoinedObject,
  Object,
  ObjectFilters,
  ObjectQuery,
  PropertyDefinition,
} from "@paradicms/models";
import * as React from "react";
import {ObjectFacetedSearchQuery} from "@paradicms/lunr";
import {Grid} from "@material-ui/core";
import {ObjectFacetsGrid} from "./ObjectFacetsGrid";
import {ObjectsGallery} from "./ObjectsGallery";

export const ObjectFacetedSearchGrid: React.FunctionComponent<{
  collections: readonly Collection[];
  images: readonly Image[];
  institutions: readonly Institution[];
  objects: readonly Object[];
  onChangeFilters: (filters: ObjectFilters) => void;
  onChangePage: (page: number) => void;
  propertyDefinitions: readonly PropertyDefinition[];
  page: number; // From 0
  query: ObjectQuery;
  renderInstitutionLink?: (
    institution: Institution,
    children: React.ReactNode
  ) => React.ReactNode;
  renderObjectLink: (
    object: JoinedObject,
    children: React.ReactNode
  ) => React.ReactNode;
}> = ({
  collections,
  images,
  institutions,
  objects,
  onChangeFilters,
  onChangePage,
  page,
  propertyDefinitions,
  renderInstitutionLink,
  renderObjectLink,
  query,
}) => {
  const totalObjectsCount = objects.length;

  return (
    <ObjectFacetedSearchQuery
      collections={collections}
      images={images}
      institutions={institutions}
      objects={objects}
      propertyDefinitions={propertyDefinitions}
      query={query}
    >
      {({objectFacets, objects}) => (
        <Grid container direction="column" spacing={2}>
          <Grid item>
            <Grid container>
              <Grid item xs={10}>
                {objects.length > 0 ? (
                  <ObjectsGallery
                    objects={objects}
                    onChangePage={onChangePage}
                    page={page}
                    renderInstitutionLink={renderInstitutionLink}
                    renderObjectLink={renderObjectLink}
                  />
                ) : (
                  <h2 style={{textAlign: "center"}}>
                    No matching objects found.
                  </h2>
                )}
              </Grid>
              <Grid item xs={2}>
                {objects.length > 0 ? (
                  <Grid container direction="column" spacing={2}>
                    <Grid item style={{textAlign: "center"}}>
                      <span>Showing&nbsp;</span>
                      <span data-cy="objects-count">{objects.length}</span>
                      <span>&nbsp;of&nbsp;</span>
                      <span data-cy="total-objects-count">
                        {totalObjectsCount}
                      </span>
                      <span>&nbsp;objects</span>
                    </Grid>
                    <Grid item>
                      <ObjectFacetsGrid
                        facets={objectFacets}
                        filters={query.filters ?? {}}
                        onChange={onChangeFilters}
                      />
                    </Grid>
                  </Grid>
                ) : null}
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      )}
    </ObjectFacetedSearchQuery>
  );
};
