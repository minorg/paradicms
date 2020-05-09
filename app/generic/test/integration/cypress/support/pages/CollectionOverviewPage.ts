import { Page } from "./Page";

export class CollectionOverviewPage extends Page {
  constructor(kwds: {collectionUri: string, institutionUri: string}) {
    super();
    this.collectionUri = kwds.collectionUri;
    this.institutionUri = kwds.institutionUri;
  }

  readonly collectionUri: string;
  readonly institutionUri: string;

  get relativeUrl() {
    return "/institution/" + encodeURIComponent(this.institutionUri) + "/collection/" + encodeURIComponent(this.collectionUri);
  }
}