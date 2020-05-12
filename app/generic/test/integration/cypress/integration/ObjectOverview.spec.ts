import { ObjectOverviewPage } from "../support/pages/ObjectOverviewPage";
import { TestData } from "./TestData";

describe("Object overview", () => {
  const page = new ObjectOverviewPage({collectionUri: TestData.collection.uri, institutionUri: TestData.institution.uri, objectUri: TestData.object.uri});

  beforeEach(() => page.visit());

  it("should show the object title in the frame", () => {
    page.frame.cardTitle.should("have.text", TestData.object.title);
  });

  it ("should have breadcrumbs to the object", () => {
    page.frame.breadcrumbItem(1).should("have.text", "Home");
    page.frame.breadcrumbItem(2).should("have.text", "Institutions");
    page.frame.breadcrumbItem(3).should("have.text", TestData.institution.name);
    page.frame.breadcrumbItem(4).should("have.text", "Collections");
    page.frame.breadcrumbItem(5).should("have.text", TestData.collection.name);
    page.frame.breadcrumbItem(6).should("have.text", "Objects");
  });

  it("should have the subject", () => {
    cy.get(".subjects-section .card .card-title h5").should("have.text", "Subjects");
    cy.get(".subjects-section .card-body ul.list-group li.list-group-item").should("have.text", TestData.object.subject);
  });

  it("should have an image in the thumbnail", () => {
    cy.get("img[src=\"" + TestData.object.uri + "/image1/thumbnail\"]");
  });
});
