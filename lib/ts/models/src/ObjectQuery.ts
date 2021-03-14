import {ObjectFilters} from "./ObjectFilters";

export interface ObjectQuery {
  readonly filters?: ObjectFilters;
  readonly text?: string;
}
