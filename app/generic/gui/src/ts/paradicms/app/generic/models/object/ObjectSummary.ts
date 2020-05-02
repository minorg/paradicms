export interface ObjectSummary {
  collectionName?: string;
  collectionUri: string;
  description: string | null;
  institutionName?: string;
  institutionUri: string;
  rights: string | null | undefined;
  thumbnail: {
    url: string;
  } | null;
  title: string;
  uri: string;
}
