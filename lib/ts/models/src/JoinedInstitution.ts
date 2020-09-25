import {Image} from "./Image";
import {Institution} from "./Institution";

export interface JoinedInstitution extends Institution {
  readonly images: readonly Image[];
}
