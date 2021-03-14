import {ModelRdfReader} from "./ModelRdfReader";
import {GuiMetadata} from "@paradicms/models";
import {PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";

export class GuiMetadataRdfReader extends ModelRdfReader<GuiMetadata> {
  read(): GuiMetadata {
    return this.deleteUndefined({
      documentTitle: this.readOptionalLiteral(
        PARADICMS.guiDocumentTitle
      )?.toString(),
      navbarTitle: this.readOptionalLiteral(
        PARADICMS.guiNavbarTitle
      )?.toString(),
    });
  }

  static readAll(store: IndexedFormula) {
    return ModelRdfReader._readAll<GuiMetadata>(
      node => new GuiMetadataRdfReader(node, store),
      store,
      PARADICMS.GuiMetadata
    );
  }
}
