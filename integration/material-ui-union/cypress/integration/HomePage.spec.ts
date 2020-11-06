import {HomePage} from "../support/pages/HomePage";
import {InstitutionPage} from "../support/pages/InstitutionPage";
import {Fixtures, InstitutionFixture} from "./Fixtures";

describe("Home", () => {
  let institutions: InstitutionFixture[];
  const page = new HomePage();

  before(() => {
    Fixtures.institutions.then(institutions_ => {
      institutions = institutions_;
    });
  });

  beforeEach(() => page.visit());

  it("should open the home page", () => {
    page.frame.cardTitle.should("have.text", "Institutions");
  });

  it("should show institutions", () => {
    for (const institution of institutions) {
      page
        .institutionLink(institution.uri)
        .should("have.text", institution.name);
    }
  });

  it("should go to the institution page when clicking on an institution", () => {
    const institution = institutions[0];
    page.institutionLink(institution.uri).click();
    cy.url().should("eq", new InstitutionPage(institution.uri).absoluteUrl);
  });
});
