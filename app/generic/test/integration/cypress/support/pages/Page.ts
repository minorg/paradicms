class Navbar {
  get homeLink() {
    return cy.get("nav a[href=\"/\"].nav-link");
  }

  get loginLink() {
    return cy.get("nav .nav-item a[href^=\"/api/auth0/\"]");
  }

  get searchButton() {
    return cy.get("nav button[type=\"submit\"]");
  }

  search(text: string) {
    this.searchInput.type(text);
    this.searchButton.click();
  }

  get searchInput() {
    return cy.get("nav input[type=\"search\"");
  }
}

export class Frame {
  get cardTitle() {
    return cy.get(".frame .card-title h2");
  }

  breadcrumbItem(n: number) {
    return cy.get(".frame ol.breadcrumb li:nth-of-type(" + n + ")");
  }

  readonly navbar = new Navbar();
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