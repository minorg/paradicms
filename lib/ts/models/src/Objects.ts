import {Collection} from "./Collection";
import {Institution} from "./Institution";
import {Object} from "./Object";
import {JoinedObject} from "./JoinedObject";
import {ObjectFilters} from "./ObjectFilters";
import {Image} from "./Image";
import {StringFilter} from "./StringFilter";
import {PropertyDefinition} from "./PropertyDefinition";
import {ObjectPropertyFacet} from "./ObjectPropertyFacet";
import {ObjectFacets} from "./ObjectFacets";

export class Objects {
  static facetize(
    propertyDefinitions: readonly PropertyDefinition[],
    objects: readonly Object[]
  ): ObjectFacets {
    const propertyFacets: ObjectPropertyFacet[] = [];
    const objectsWithProperties = objects.filter(object => object.properties);
    for (const propertyDefinition of propertyDefinitions) {
      if (!propertyDefinition.faceted) {
        continue;
      }
      const facetObjects: Object[] = [];
      const facetValues: {[index: string]: number} = {};
      for (const object of objectsWithProperties) {
        let includeObject = false;
        for (const property of object.properties!) {
          if (property.uri === propertyDefinition.uri) {
            const count = facetValues[property.value.toString()];
            if (!count) {
              facetValues[property.value.toString()] = 1;
            } else {
              facetValues[property.value.toString()] = count + 1;
            }
            includeObject = true;
          }
        }
        if (includeObject) {
          facetObjects.push(object);
        }
      }
      if (facetObjects.length > 0) {
        propertyFacets.push({
          definition: propertyDefinition,
          objects: facetObjects,
          values: Object.keys(facetValues).map(facetValue => ({
            count: facetValues[facetValue],
            value: facetValue,
          })),
        });
      }
    }
    return {properties: propertyFacets};
  }

  static filter(kwds: {
    filters: ObjectFilters;
    objects: readonly Object[];
  }): readonly Object[] {
    let {filters, objects} = kwds;

    const filterStrings = (kwds: {
      filter?: StringFilter;
      getObjectValues: (object: Object) => readonly string[] | undefined;
      objects: readonly Object[];
    }): readonly Object[] => {
      const {filter, getObjectValues, objects} = kwds;
      if (!filter) {
        return objects;
      }
      const excludeValues = filter.exclude ?? [];
      const includeValues = filter.include ?? [];
      if (excludeValues.length === 0 && includeValues.length === 0) {
        return objects;
      }
      return objects.filter(object => {
        let objectValues = getObjectValues(object);
        if (!objectValues) {
          objectValues = [];
        }

        if (excludeValues.length > 0) {
          // If an object has any value that is excluded, then exclude the object
          for (const objectValue of objectValues) {
            if (
              excludeValues.some(excludeValue => excludeValue === objectValue)
            ) {
              return false;
            }
          }
        }

        if (includeValues.length > 0) {
          // If the object has any value that is included, then include the object
          // Conversely, if any values are included and an object doesn't have one of them, exclude the object.
          let include = false;
          for (const objectValue of objectValues) {
            if (
              includeValues.some(includeValue => includeValue === objectValue)
            ) {
              include = true;
              break;
            }
          }
          if (!include) {
            return false;
          }
        }

        return true;
      });
    };

    objects = filterStrings({
      filter: filters.collectionUris,
      getObjectValues: object => object.collectionUris,
      objects,
    });

    objects = filterStrings({
      filter: filters.institutionUris,
      getObjectValues: object => [object.institutionUri],
      objects,
    });

    if (filters.properties) {
      for (const propertyFilter of filters.properties) {
        objects = filterStrings({
          filter: propertyFilter,
          getObjectValues: object =>
            (object.properties ?? [])
              .filter(
                property =>
                  property.uri === propertyFilter.propertyDefinitionUri
              )
              .map(property => property.value.toString()),
          objects,
        });
      }
    }

    return objects;
  }

  static indexByCollectionUri(
    objects: readonly Object[]
  ): {[index: string]: readonly Object[]} {
    const result: {[index: string]: Object[]} = {};
    for (const object of objects) {
      for (const collectionUri of object.collectionUris) {
        let collectionObjects = result[collectionUri];
        if (!collectionObjects) {
          result[collectionUri] = collectionObjects = [];
        }
        collectionObjects.push(object);
      }
    }
    return result;
  }

  static join(kwds: {
    collectionsByUri: {[index: string]: Collection};
    imagesByDepictsUri: {[index: string]: readonly Image[]};
    institutionsByUri: {[index: string]: Institution};
    objects: readonly Object[];
  }): readonly JoinedObject[] {
    const {
      collectionsByUri,
      imagesByDepictsUri,
      institutionsByUri,
      objects,
    } = kwds;
    return objects.map(object => {
      const collections: Collection[] = [];
      for (const collectionUri of object.collectionUris) {
        const collection = collectionsByUri[collectionUri];
        if (collection) {
          collections.push(collection);
        }
      }
      if (collections.length === 0) {
        throw new EvalError(
          "unable to resolve any collection URIs: " +
            object.collectionUris.join(" ")
        );
      }

      const images = imagesByDepictsUri[object.uri];

      const institution = institutionsByUri[object.institutionUri];
      if (!institution) {
        throw new EvalError(
          "unable to resolve institution " + object.institutionUri
        );
      }

      return {
        collections,
        images: images ?? [],
        institution,
        ...object,
      };
    });
  }
}
