import {Page} from "./Page";
import * as qs from "qs";
import {
  Fixtures,
  InstitutionFixture,
  ObjectFixture,
} from "../../integration/Fixtures";
import {ObjectPage} from "./ObjectPage";

class StringFacet {
  constructor(private readonly id: string) {}

  toggleOpen() {
    return cy
      .get(
        '[data-cy="' +
          this.id +
          '-facet"] .MuiButtonBase-root.MuiAccordionSummary-root'
      )
      .click();
  }

  toggleValue(value: string) {
    return cy
      .get('[data-cy="' + this.id + '-facet"]')
      .contains(value)
      .click();
  }
}

export class ObjectFacets {
  readonly creator = new StringFacet("creator");
  readonly subject = new StringFacet("subject");
}

export class ObjectsGallery {
  get endObjectIndex() {
    return cy.get('[data-cy="end-object-index"]');
  }

  getObjectLink(object: ObjectFixture) {
    const objectLink = new ObjectPage({
      institutionUri: object.institutionUri,
      objectTitle: object.title,
      objectUri: object.uri,
    }).relativeUrl;
    return cy.get('a[href="' + objectLink + '"] .MuiCardHeader-title');
  }

  get objectsCount() {
    return cy.get('[data-cy="objects-count"]');
  }

  get startObjectIndex() {
    return cy.get('[data-cy="start-object-index"]');
  }
}

export class SearchPage extends Page {
  constructor(readonly text: string) {
    super();
  }

  readonly objectFacets = new ObjectFacets();
  readonly objectsGallery = new ObjectsGallery();

  readonly relativeUrl =
    "/search/?" + qs.stringify({query: JSON.stringify({text: this.text})});
}
