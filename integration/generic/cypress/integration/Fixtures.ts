export interface CollectionFixture {
  institutionUri: string;
  title: string;
  uri: string;
}

export interface InstitutionFixture {
  name: string;
  uri: string;
}

export interface ObjectFixture {
  institutionUri: string;
  title: string;
  uri: string;
}

export class Fixtures {
  static get collections(): Cypress.Chainable<CollectionFixture[]> {
    return cy.fixture("collection.json");
  }

  static get institutions(): Cypress.Chainable<InstitutionFixture[]> {
    return cy.fixture("institution.json");
  }

  static get objects(): Cypress.Chainable<ObjectFixture[]> {
    return cy.fixture("object.json");
  }
}
