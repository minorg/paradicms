export abstract class Page {
  abstract readonly relativeUrl: string;

  frameCardTitle() {
    return cy.get(".frame .card-title h2");
  }

  visit(): void {
    cy.visit(this.relativeUrl);
  }
}