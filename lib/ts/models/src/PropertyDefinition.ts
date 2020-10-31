export interface PropertyDefinition {
  readonly faceted?: boolean | null;
  readonly fullTextSearchable?: boolean | null;
  readonly label: string;
  readonly uri: string;
}
