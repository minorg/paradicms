import { reset } from "../support/reset";
import { HomePage } from "../support/pages/HomePage";

describe("Home", () => {
  beforeEach(reset);

  const page = new HomePage();

  it("should open the home page", () => {
    page.visit();
  })
});
