import {ModelRdfReader} from "./ModelRdfReader";
import {RightsStatement} from "@paradicms/models";
import {DCTERMS, PARADICMS, SKOS} from "./vocabularies";
import {IndexedFormula} from "rdflib";

export class RightsStatementRdfReader extends ModelRdfReader<RightsStatement> {
  read(): RightsStatement {
    return this.deleteUndefined({
      definition: this.readOptionalLiteral(SKOS.definition)?.toString(),
      description: this.readOptionalLiteral(DCTERMS.description)?.toString(),
      identifier: this.readRequiredLiteral(DCTERMS.identifier).toString(),
      prefLabel: this.readRequiredLiteral(SKOS.prefLabel).toString(),
      uri: this.nodeUri,
    });
  }

  static readAll(store: IndexedFormula) {
    return ModelRdfReader._readAll<RightsStatement>(
      node => new RightsStatementRdfReader(node, store),
      store,
      PARADICMS.RightsStatement
    );
  }
}
