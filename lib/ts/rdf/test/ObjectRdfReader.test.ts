import {expect} from "chai";
import {describe} from "mocha";
import {IndexedFormula} from "rdflib";
import {parseTestData} from "./parseTestData";
import {ObjectRdfReader} from "../src/ObjectRdfReader";
import {PropertyDefinitionRdfReader} from "../src/PropertyDefinitionRdfReader";

describe("Object RDF reader", () => {
  let store: IndexedFormula;

  before(function(this: any) {
    // @ts-ignore
    this.timeout(10000);
    store = parseTestData();
  });

  it("should read all objects from the store", function(this: any) {
    this.timeout(10000);
    const propertyDefinitions = PropertyDefinitionRdfReader.readAll(store);
    const models = ObjectRdfReader.readAll(propertyDefinitions, store);
    expect(models).to.have.length(90);
    models.forEach(model => {
      expect(model.collectionUris).to.not.be.empty;
      expect(model.institutionUri).to.not.be.empty;
      expect(model.properties).to.not.be.empty;
      model.properties!.forEach(property => {
        expect(property.propertyDefinitionUri.trim()).to.not.be.empty;
        expect(
          propertyDefinitions.find(
            propertyDefinition =>
              propertyDefinition.uri === property.propertyDefinitionUri
          )
        ).to.not.be.undefined;
        expect(property.value.trim()).to.not.be.empty;
      });
      expect(model.rights).to.not.be.undefined;
      expect(model.title.trim()).to.not.be.empty;
      expect(model.uri.trim()).to.not.be.empty;
    });
  });
});
