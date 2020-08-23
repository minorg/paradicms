import {Page} from "./Page";
import {CollectionPage} from "./CollectionPage";
import {encodeFileName} from "../../../../../lib/ts/base/src";

export class InstitutionPage extends Page {
  constructor(readonly institutionUri: string) {
    super();
  }

  collectionLink(kwds: {collectionUri: string; institutionUri: string}) {
    const collectionHref = new CollectionPage(kwds).relativeUrl;
    return cy.get('a[href="' + collectionHref + '"]');
  }

  readonly relativeUrl = `/institution/${encodeFileName(this.institutionUri)}/`;
}
