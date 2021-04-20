import {ModelRdfReader} from "./ModelRdfReader";
import {Institution, RightsStatement} from "@paradicms/models";
import {FOAF, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {checkNotNullish} from "./checkNotNullish";
import {ModelNode} from "./ModelNode";
import {RightsStatementRdfReader} from "./RightsStatementRdfReader";

export class InstitutionRdfReader extends ModelRdfReader<Institution> {
  constructor(
    node: ModelNode,
    private readonly rightsStatements: readonly RightsStatement[],
    store: IndexedFormula
  ) {
    super(node, store);
  }

  read(): Institution {
    return {
      name: this.readRequiredLiteral(FOAF.name_).toString(),
      rights: checkNotNullish(
        new RightsRdfReader(
          this.node,
          this.rightsStatements,
          this.store
        ).read(),
        "institution must have a non-nullish rights"
      ),
      uri: this.nodeUri,
    };
  }

  static readAll(store: IndexedFormula) {
    const rightsStatements = RightsStatementRdfReader.readAll(store);
    return ModelRdfReader._readAll<Institution>(
      node => new InstitutionRdfReader(node, rightsStatements, store),
      store,
      PARADICMS.Institution
    );
  }
}
