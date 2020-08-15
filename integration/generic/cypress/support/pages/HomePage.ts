import {Page} from "./Page";
import {InstitutionPage} from "./InstitutionPage";

export class HomePage extends Page {
  readonly relativeUrl = "/";

  institutionLink(institutionUri: string) {
    const institutionHref = new InstitutionPage(institutionUri).relativeUrl;
    return cy.get('a[href="' + institutionHref + '"]');
  }
}
