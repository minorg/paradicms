import {ObjectPage} from "../support/pages/ObjectPage";
import {Fixtures, InstitutionFixture, ObjectFixture} from "./Fixtures";

describe("Object overview", () => {
  let institution: InstitutionFixture;
  let object: ObjectFixture;
  let page: ObjectPage;

  before(() => {
    Fixtures.institutions.then(institutions => {
      institution = institutions[0];
      Fixtures.objects.then(objects => {
        const institutionObjects = objects.filter(
          object => object.institutionUri === institution.uri
        );
        object = institutionObjects[0];
        page = new ObjectPage({
          institutionUri: institution.uri,
          objectTitle: object.title,
          objectUri: object.uri,
        });
      });
    });
  });

  beforeEach(() => page.visit());

  it("should show the object title in the frame", () => {
    page.frame.cardTitle.should("have.text", "Object - " + object.title);
  });

  it("should have breadcrumbs to the object", () => {
    page.frame.breadcrumbItem(1).should("have.text", "Home");
    page.frame.breadcrumbItem(2).should("have.text", "Institutions");
    page.frame.breadcrumbItem(3).should("have.text", institution.name);
    page.frame.breadcrumbItem(4).should("have.text", "Objects");
    page.frame.breadcrumbItem(5).should("have.text", object.title);
  });

  // it("should have the subject", () => {
  //   page.subjects.should("have.text", object.subject);
  // });

  it("should have an image in the thumbnail", () => {
    page.carouselThumbnail;
  });
});
