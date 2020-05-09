import { HomePage } from "../support/pages/HomePage";
import { InstitutionOverviewPage } from "../support/pages/InstitutionOverviewPage";

describe("Home", () => {
  const page = new HomePage();

  beforeEach(() => page.visit());

  it("should open the home page", () => {
    page.frameCardTitle.should("have.text", "Institutions");
  });

  it ("should show institutions", () => {
    const institutionUri = "http://example.com/institution"
    const institutionHref = new InstitutionOverviewPage(institutionUri).relativeUrl;
    cy.get("a[href=\"" + institutionHref + "\"]").should("have.text", "Test institution");
  });
});
