import {ObjectFilters} from "ObjectFilters";
import {PropertyFilter} from "PropertyFilter";

export class ObjectFiltersState {
  constructor(private objectFilters: ObjectFilters) {
    this.objectFilters = objectFilters;
  }

  getPropertyFilter(propertyDefinitionUri: string): PropertyFilter | undefined {
    return this.objectFilters.properties?.find(
      propertyFilter =>
        propertyFilter.propertyDefinitionUri === propertyDefinitionUri
    );
  }

  removePropertyFilter(propertyDefinitionUri: string): void {
    const {
      properties: oldPropertyFilters,
      ...otherObjectFilters
    } = this.objectFilters;
    if (!oldPropertyFilters) {
      return;
    }

    // Remove the key's filter and then add it back
    const newPropertyFilters = oldPropertyFilters.filter(
      propertyFilter =>
        propertyFilter.propertyDefinitionUri !== propertyDefinitionUri
    );

    if (newPropertyFilters.length === 0) {
      this.objectFilters = otherObjectFilters;
    } else {
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

    let newPropertyFilters: PropertyFilter[];
    if (!oldPropertyFilters) {
      newPropertyFilters = [];
    } else {
      // Remove the key's filter and then add it back
      newPropertyFilters = oldPropertyFilters.filter(
        propertyFilter =>
          propertyFilter.propertyDefinitionUri !==
          propertyFilter.propertyDefinitionUri
      );
    }
    newPropertyFilters.push(propertyFilter);

    this.objectFilters = {
      ...otherObjectFilters,
      properties: newPropertyFilters,
    };
  }

  get snapshot(): ObjectFilters {
    return this.objectFilters;
  }
}
