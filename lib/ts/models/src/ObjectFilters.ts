import {StringFilter} from "./StringFilter";
import {PropertyFilter} from "./PropertyFilter";

export interface ObjectFilters {
  readonly collectionUris?: StringFilter;
  readonly institutionUris?: StringFilter;
  readonly properties?: readonly PropertyFilter[];
}
