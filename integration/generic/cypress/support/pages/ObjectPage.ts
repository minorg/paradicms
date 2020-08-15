import {Page} from "./Page";
import sanitize from "sanitize-filename";

export class ObjectPage extends Page {
  constructor(kwds: {
    institutionUri: string;
    objectTitle: string;
    objectUri: string;
  }) {
    super();
    this.institutionUri = kwds.institutionUri;
    this.objectTitle = kwds.objectTitle;
    this.objectUri = kwds.objectUri;
  }

  readonly institutionUri: string;
  readonly objectTitle: string;
  readonly objectUri: string;

  get carouselThumbnail() {
    return cy.get(
      `img[src="https://place-hold.it/600x600?text=${this.objectTitle}Image0`
    );
  }

  // get subjects() {
  //   cy.get('[data-cy="subjects-section"] .MuiCardHeader-title').should(
  //     "have.text",
  //     "Subjects"
  //   );
  //   return cy.get('[data-cy="subjects-section"] ul div');
  // }

  get relativeUrl() {
    return `/institution/${sanitize(this.institutionUri)}/object/${sanitize(
      this.objectUri
    )}/`;
  }
}
