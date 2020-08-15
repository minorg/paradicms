import {Page} from "./Page";
import {ObjectFacets, ObjectsGallery} from "./SearchResultsPage";
import sanitize from "sanitize-filename";

export class CollectionOverviewPage extends Page {
  constructor(kwds: {collectionUri: string; institutionUri: string}) {
    super();
    this.collectionUri = kwds.collectionUri;
    this.institutionUri = kwds.institutionUri;
  }

  readonly collectionUri: string;
  readonly institutionUri: string;

  readonly objectFacets = new ObjectFacets();
  readonly objectsGallery = new ObjectsGallery();

  get relativeUrl() {
    return `/institution/${sanitize(this.institutionUri)}/collection/${sanitize(
      this.collectionUri
    )}/`;
  }
}
