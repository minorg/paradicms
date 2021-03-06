import {ModelRdfReader} from "./ModelRdfReader";
import {License} from "@paradicms/models";
import {DCTERMS, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";

export class LicenseRdfReader extends ModelRdfReader<License> {
  read(): License {
    return this.deleteUndefined({
      identifier: this.readRequiredLiteral(DCTERMS.identifier).toString(),
      title: this.readRequiredLiteral(DCTERMS.title).toString(),
      uri: this.nodeUri,
    });
  }

  static readAll(store: IndexedFormula) {
    return ModelRdfReader._readAll<License>(
      node => new LicenseRdfReader(node, store),
      store,
      PARADICMS.License
    );
  }
}
