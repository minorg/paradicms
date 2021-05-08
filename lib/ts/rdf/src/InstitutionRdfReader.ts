import {ModelRdfReader} from "./ModelRdfReader";
import {Institution, License, RightsStatement} from "@paradicms/models";
import {FOAF, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {checkNotNullish} from "./checkNotNullish";
import {ModelNode} from "./ModelNode";
import {RightsStatementRdfReader} from "./RightsStatementRdfReader";
import {LicenseRdfReader} from "./LicenseRdfReader";

export class InstitutionRdfReader extends ModelRdfReader<Institution> {
  constructor(
    private readonly licenses: readonly License[],
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
          this.licenses,
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
    const licenses = LicenseRdfReader.readAll(store);
    const rightsStatements = RightsStatementRdfReader.readAll(store);
    return ModelRdfReader._readAll<Institution>(
      node => new InstitutionRdfReader(licenses, node, rightsStatements, store),
      store,
      PARADICMS.Institution
    );
  }
}
