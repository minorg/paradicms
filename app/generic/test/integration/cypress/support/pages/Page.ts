class Footer {
  get privacyLink() {
    return cy.get("#frame footer a[href=\"/privacy\"]");
  }
}

class Frame {
  get cardTitle() {
    return cy.get("#frame-card-header");
  }

  breadcrumbItem(n: number) {
    return cy.get("#frame-breadcrumbs li:nth-of-type(" + (n*2-1) + ")");
  }

  readonly footer = new Footer();
  readonly navbar = new Navbar();
}

class Navbar {
  get homeLink() {
    return cy.get("#navbar a[href=\"/\"]");
  }

  get loginLink() {
    return cy.get("#navbar a[href^=\"/api/auth0/\"]");
  }

  get searchButton() {
    return cy.get("#navbar #search-button");
  }

  search(text: string) {
    this.searchInput.type(text);
    this.searchButton.click();
  }

  get searchInput() {
    return cy.get("#navbar #search-input");
  }
}

export abstract class Page {
  get absoluteUrl() {
    return Cypress.config().baseUrl + this.relativeUrl;
  }

  readonly frame = new Frame();

  abstract readonly relativeUrl: string;

  visit(): void {
    cy.visit(this.relativeUrl);
    cy.url().should("eq", this.absoluteUrl);
  }
}