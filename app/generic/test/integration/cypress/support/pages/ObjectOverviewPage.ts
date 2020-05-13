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

  get carouselThumbnail() {
    return cy.get("img[src=\"" + this.objectUri + "/image1/thumbnail\"]");
  }

  get subjects() {
    cy.get(".subjects-section .card .card-title h5").should("have.text", "Subjects");
    return cy.get(".subjects-section .card-body ul.list-group li.list-group-item");
  }

  get relativeUrl() {
    return "/institution/" + encodeURIComponent(this.institutionUri) + "/collection/" + encodeURIComponent(this.collectionUri) + "/object/" + encodeURIComponent(this.objectUri);
  }
}
