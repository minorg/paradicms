import {ImageDimensions} from "./ImageDimensions";
import {Rights} from "./Rights";

export interface Image {
  readonly depictsUri: string;
  readonly exactDimensions?: ImageDimensions;
  readonly institutionUri: string;
  readonly maxDimensions?: ImageDimensions;
  readonly originalImageUri?: string;
  readonly rights?: Rights;
  readonly uri: string;
}
