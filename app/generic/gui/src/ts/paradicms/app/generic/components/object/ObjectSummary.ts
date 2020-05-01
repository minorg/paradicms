export interface ObjectSummary {
  collectionName?: string;
  collectionUri: string;
  description: string | null | undefined;
  institutionName?: string;
  institutionUri: string;
  rights: string | null | undefined;
  thumbnail:
    | {
        height: number | null | undefined;
        url: string;
        width: number | null | undefined;
      }
    | null
    | undefined;
  title: string;
  uri: string;
}
