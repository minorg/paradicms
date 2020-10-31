import {ObjectFilters} from "ObjectFilters";
import {PropertyFilter} from "PropertyFilter";
import {Property} from "Property";

export class ObjectFiltersState {
  constructor(private objectFilters: ObjectFilters) {
    this.objectFilters = objectFilters;
  }

  getExcludedProperties(): readonly Property[] {
    const excludedProperties: Property[] = [];
    if (this.objectFilters.properties) {
      for (const propertyFilter of this.objectFilters.properties) {
        if (!propertyFilter.exclude) {
          continue;
        }
        for (const exclude of propertyFilter.exclude) {
          excludedProperties.push({
            propertyDefinitionUri: propertyFilter.propertyDefinitionUri,
            value: exclude,
          });
        }
      }
    }
    return excludedProperties;
  }

  getIncludedProperties(): readonly Property[] {
    const includedProperties: Property[] = [];
    if (this.objectFilters.properties) {
      for (const propertyFilter of this.objectFilters.properties) {
        if (!propertyFilter.include) {
          continue;
        }
        for (const include of propertyFilter.include) {
          includedProperties.push({
            propertyDefinitionUri: propertyFilter.propertyDefinitionUri,
            value: include,
          });
        }
      }
    }
    return includedProperties;
  }

  getPropertyFilter(propertyDefinitionUri: string): PropertyFilter | undefined {
    return this.objectFilters.properties?.find(
      propertyFilter =>
        propertyFilter.propertyDefinitionUri === propertyDefinitionUri
    );
  }

  removePropertyFilter(propertyDefinitionUri: string): void {
    console.debug("remove property filter: " + propertyDefinitionUri);

    const {
      properties: oldPropertyFilters,
      ...otherObjectFilters
    } = this.objectFilters;
    if (!oldPropertyFilters) {
      console.debug("no property filters to remove");
      return;
    }

    console.debug("old property filters:", JSON.stringify(oldPropertyFilters));

    // Remove the key's filter and then add it back
    const newPropertyFilters = oldPropertyFilters.filter(
      propertyFilter =>
        propertyFilter.propertyDefinitionUri !== propertyDefinitionUri
    );

    if (newPropertyFilters.length === 0) {
      console.debug("removed all property filters");
      this.objectFilters = otherObjectFilters;
    } else {
      console.debug(
        "new property filters:",
        JSON.stringify(newPropertyFilters)
      );
      this.objectFilters = {
        ...otherObjectFilters,
        properties: newPropertyFilters,
      };
    }
  }

  setPropertyFilter(propertyFilter: PropertyFilter): void {
    const {
      properties: oldPropertyFilters,
      ...otherObjectFilters
    } = this.objectFilters;

    console.debug("old property filters:", JSON.stringify(oldPropertyFilters));

    let newPropertyFilters: PropertyFilter[];
    if (!oldPropertyFilters) {
      newPropertyFilters = [];
    } else {
      // Remove the key's filter and then add it back
      newPropertyFilters = oldPropertyFilters.filter(
        oldPropertyFilter =>
          oldPropertyFilter.propertyDefinitionUri !==
          propertyFilter.propertyDefinitionUri
      );
    }
    newPropertyFilters.push(propertyFilter);

    console.debug("new property filters:", JSON.stringify(newPropertyFilters));

    this.objectFilters = {
      ...otherObjectFilters,
      properties: newPropertyFilters,
    };
  }

  get snapshot(): ObjectFilters {
    return this.objectFilters;
  }
}
