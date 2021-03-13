import {Property} from "./Property";
import {Rights} from "./Rights";

export interface Object {
  readonly abstract?: string;
  readonly collectionUris: readonly string[];
  readonly institutionUri: string;
  readonly properties?: readonly Property[];
  readonly rights?: Rights;
  readonly title: string;
  readonly uri: string;
}
