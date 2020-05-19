import { Page } from "./Page";
import * as qs from "qs";
import { TestData } from "../../integration/TestData";
import { ObjectOverviewPage } from "./ObjectOverviewPage";

class StringFacet {
  constructor(private readonly id: string) {
  }

  toggleOpen() {
    return cy.get("." + this.id + "-facet a.facet-header-text").click();
  }

  toggleValue(value: string) {
    return cy.get("." + this.id + "-facet").contains(value).click();
  }
}

export class ObjectFacets {
  readonly subject = new StringFacet("subject");
}

export class ObjectsGallery {
  get endObjectIndex() {
    return cy.get(".end-object-index");
  }

  getObjects(objects: (typeof TestData.object)[]): void {
    for (const object of objects) {
      const objectLink = new ObjectOverviewPage({collectionUri: TestData.collection.uri, institutionUri: TestData.institution.uri, objectUri: object.uri}).relativeUrl;
      cy.get("a[href=\"" + objectLink + "\"] .MuiCardHeader-title").should("have.text", object.title);
      // cy.get("img[src=\"" + object.uri + "/image0/square_thumbnail\"]");
    }
  }

  get objectsCount() {
    return cy.get(".objects-count");
  }

  get startObjectIndex() {
    return cy.get(".start-object-index");
  }

}

export class SearchResultsPage extends Page {
  constructor(readonly text: string) {
    super();
  }

  readonly objectFacets = new ObjectFacets();
  readonly objectsGallery = new ObjectsGallery();

  readonly relativeUrl = "/search?" + qs.stringify({text: this.text});
}