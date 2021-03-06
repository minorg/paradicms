import {ModelRdfReader} from "./ModelRdfReader";
import {PropertyDefinition} from "@paradicms/models";
import {PARADICMS, RDFS} from "./vocabularies";
import {IndexedFormula} from "rdflib";

export class PropertyDefinitionRdfReader extends ModelRdfReader<
  PropertyDefinition
> {
  read(): PropertyDefinition {
    return this.deleteUndefined({
      faceted: this.readOptionalLiteral(PARADICMS.faceted)?.toBoolean(),
      label: this.readRequiredLiteral(RDFS.label).toString(),
      fullTextSearchable: this.readOptionalLiteral(
        PARADICMS.fullTextSearchable
      )?.toBoolean(),
      uri: this.nodeUri,
    });
  }

  static readAll(store: IndexedFormula) {
    return ModelRdfReader._readAll<PropertyDefinition>(
      node => new PropertyDefinitionRdfReader(node, store),
      store,
      PARADICMS.PropertyDefinition
    );
  }
}
