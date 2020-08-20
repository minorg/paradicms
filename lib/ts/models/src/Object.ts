// import {ObjectJson} from "~/graphql/types";
//
// export type Object = Pick<
//   ObjectJson,
//   | "collectionUris"
//   | "institutionUri"
//   | "properties"
//   | "rights"
//   | "title"
//   | "uri"
// >;

import {Property} from "./Property";
import {Rights} from "./Rights";

export interface Object {
  readonly collectionUris: readonly string[];
  readonly institutionUri: string;
  readonly properties?: readonly Property[] | null;
  readonly rights?: Rights | null;
  readonly title: string;
  readonly uri: string;
}