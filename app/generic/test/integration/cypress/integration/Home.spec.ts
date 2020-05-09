import { HomePage } from "../support/pages/HomePage";
import { InstitutionOverviewPage } from "../support/pages/InstitutionOverviewPage";

describe("Home", () => {
  const page = new HomePage();

  beforeEach(() => page.visit());

  it("should open the home page", () => {
    page.frameCardTitle.should("have.text", "Institutions");
  });

  it ("should show institutions", () => {
    page.institutionLink("http://example.com/institution").should("have.text", "Test institution");
  });

  it("should go to the institution page when clicking on an institution", () => {
    const institutionUri = "http://example.com/institution";
    page.institutionLink(institutionUri).click();
    cy.url().should("eq", new InstitutionOverviewPage(institutionUri).absoluteUrl);
  });
});
