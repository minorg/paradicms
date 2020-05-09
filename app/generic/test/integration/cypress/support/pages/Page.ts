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

export abstract class Page {
  get absoluteUrl() {
    return Cypress.config().baseUrl + this.relativeUrl;
  }

  readonly navbar = new Navbar();

  abstract readonly relativeUrl: string;

  get frameCardTitle() {
    return cy.get(".frame .card-title h2");
  }

  visit(): void {
    cy.visit(this.relativeUrl);
    cy.url().should("eq", this.absoluteUrl);
  }
}