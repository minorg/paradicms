import {Institution} from "./Institution";
import {Image} from "./Image";

export interface JoinedCollection {
  readonly images: readonly Image[];
  readonly institution: Institution;
  readonly title: string;
  readonly uri: string;
}
