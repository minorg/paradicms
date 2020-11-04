import {
  Collection,
  Image,
  Images,
  Institution,
  JoinedObject,
  Models,
  Object,
  ObjectFacets,
  ObjectQuery,
  Objects,
  PropertyDefinition,
} from "@paradicms/models";
import * as React from "react";
import {ObjectIndex} from "./ObjectIndex";

/**
 * Component that encapsulates the logic of searching, filtering, joining, and faceting objects, then calls a render prop with the results.
 */
export const ObjectFacetedSearch: React.FunctionComponent<{
  children: (kwds: {
    objectFacets: ObjectFacets;
    objects: readonly JoinedObject[];
  }) => React.ReactNode;
  collections: readonly Collection[];
  images: readonly Image[];
  institutions: readonly Institution[];
  objects: readonly Object[];
  propertyDefinitions: readonly PropertyDefinition[];
  query: ObjectQuery;
}> = ({
  children,
  collections,
  images,
  institutions,
  objects,
  propertyDefinitions,
  query,
}) => {
  const index = React.useMemo(
    () => new ObjectIndex(objects, propertyDefinitions),
    [objects, propertyDefinitions]
  );

  const collectionsByUri = React.useMemo(() => Models.indexByUri(collections), [
    collections,
  ]);
  const imagesByDepictsUri = React.useMemo(
    () => Images.indexByDepictsUri(images),
    [images]
  );
  const institutionsByUri = React.useMemo(
    () => Models.indexByUri(institutions),
    [institutions]
  );

  const results = React.useMemo(() => {
    if (!index) {
      return null;
    }

    console.info("query " + JSON.stringify(query));

    const searchedObjects: readonly Object[] = query.text
      ? index.search(query.text)
      : objects;
    const filteredObjects: readonly Object[] = query.filters
      ? Objects.filter({
          filters: query.filters,
          objects: searchedObjects,
        })
      : searchedObjects;
    const joinedObjects = Objects.join({
      collectionsByUri,
      imagesByDepictsUri,
      institutionsByUri,
      objects: filteredObjects,
    });

    return {
      objectFacets: Objects.facetize(propertyDefinitions, objects),
      objects: joinedObjects,
    };
  }, [index, query]);

  if (!results) {
    return null;
  }

  return <>{children(results)}</>;
};
