import {expect} from "chai";
import {describe} from "mocha";
import {IndexedFormula} from "rdflib";
import {parseTestData} from "./parseTestData";
import {PARADICMS} from "../src/vocabularies";

describe("PropertyDefinition RDF reader", () => {
  let store: IndexedFormula;

  before(function(this: any) {
    // @ts-ignore
    this.timeout(10000);
    store = parseTestData();
  });

  it("should read test property definitions", () => {
    const nodes = store.each(
      undefined,
      undefined,
      PARADICMS.PropertyDefinition
    );
    expect(nodes).to.have.length(36);
  });
});
