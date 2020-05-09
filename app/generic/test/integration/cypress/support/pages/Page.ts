export abstract class Page {
  abstract readonly relativeUrl: string;

  visit(): void {
    cy.visit(this.relativeUrl);
  }
}