import { Page } from "./Page";

export class ObjectOverviewPage extends Page {
  constructor(kwds: {collectionUri: string, institutionUri: string, objectUri: string}) {
    super();
    this.collectionUri = kwds.collectionUri;
    this.institutionUri = kwds.institutionUri;
    this.objectUri = kwds.objectUri;
  }

  readonly collectionUri: string;
  readonly institutionUri: string;
  readonly objectUri: string;

  get relativeUrl() {
    return "/institution/" + encodeURIComponent(this.institutionUri) + "/collection/" + encodeURIComponent(this.collectionUri) + "/object/" + encodeURIComponent(this.objectUri);
  }
}
