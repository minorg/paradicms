export interface RightsStatement {
  readonly identifier: string;
  readonly prefLabel: string;
  readonly definition?: string;
  readonly description?: string;
  // Ignore notes
  // Ignore scopeNote
  readonly uri: string;
}
