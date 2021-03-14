import {Image} from "./Image";
import {Collection} from "./Collection";
import {Institution} from "./Institution";
import {Property} from "./Property";
import {Rights} from "./Rights";

export interface JoinedObject {
  readonly abstract?: string;
  readonly collections: readonly Collection[];
  readonly images: readonly Image[];
  readonly institution: Institution;
  readonly properties?: readonly Property[];
  readonly rights?: Rights;
  readonly title: string;
  readonly uri: string;
}
