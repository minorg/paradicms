import {PropertyDefinition} from "./PropertyDefinition";

export interface ObjectPropertyFacet {
  readonly definition: PropertyDefinition;
  readonly objects: readonly Object[];
  readonly values: readonly string[];
}
