import {StringFilter} from "./StringFilter";

export interface PropertyFilter extends StringFilter {
  readonly key: string;
}
