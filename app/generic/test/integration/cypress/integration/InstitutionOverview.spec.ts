import { InstitutionOverviewPage } from "../support/pages/InstitutionOverviewPage";
import { CollectionOverviewPage } from "../support/pages/CollectionOverviewPage";
import { HomePage } from "../support/pages/HomePage";

describe("Institution overview", () => {
  const collectionUri = "http://example.com/collection";
  const institutionUri = "http://example.com/institution"
  const page = new InstitutionOverviewPage(institutionUri);

  beforeEach(() => page.visit());

  it("should show the institution name in the frame", () => {
    page.frame.cardTitle.should("have.text", "Test institution - Collections");
  });

  it ("should have breadcrumbs to the institution", () => {
    page.frame.breadcrumbItem(1).should("have.text", "Home");
    page.frame.breadcrumbItem(2).should("have.text", "Institutions");
    page.frame.breadcrumbItem(3).should("have.text", "Test institution");
  });

  it("should have a collection link that leads to the collection overview page", () => {
    page.collectionLink({collectionUri, institutionUri}).click();
    cy.url().should("eq", new CollectionOverviewPage({collectionUri, institutionUri}).absoluteUrl);
  });

  it("should return home via breadcrumb", () => {
    page.frame.breadcrumbItem(1).click();
    cy.url().should("eq", new HomePage().absoluteUrl);
  });
});
