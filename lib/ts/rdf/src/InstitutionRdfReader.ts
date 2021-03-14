import {ModelRdfReader} from "./ModelRdfReader";
import {Institution} from "@paradicms/models";
import {FOAF, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {checkNotNullish} from "./checkNotNullish";

export class InstitutionRdfReader extends ModelRdfReader<Institution> {
  read(): Institution {
    return {
      name: this.readRequiredLiteral(FOAF.name_).toString(),
      rights: checkNotNullish(
        new RightsRdfReader(this.node, this.store).read()
      ),
      uri: this.nodeUri,
    };
  }

  static readAll(store: IndexedFormula) {
    return ModelRdfReader._readAll<Institution>(
      node => new InstitutionRdfReader(node, store),
      store,
      PARADICMS.Institution
    );
  }
}
