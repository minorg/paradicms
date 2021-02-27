import {ModelRdfReader} from "./ModelRdfReader";
import {Collection} from "@paradicms/models";
import {DCTERMS, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";
import {RdfReaderException} from "./RdfReaderException";

export class CollectionRdfReader extends ModelRdfReader<Collection> {
  read(): Collection {
    const institutionNode = this.store.any(
      undefined,
      PARADICMS.collection,
      this.node
    );
    if (!institutionNode) {
      throw new RdfReaderException(
        "missing (<institution>, collection, <collection>) statement"
      );
    }
    if (institutionNode.termType !== "NamedNode") {
      throw new RdfReaderException(
        "expected institution node to be a named node"
      );
    }
    return {
      institutionUri: institutionNode.value,
      title: this.readRequiredLiteral(DCTERMS.title).toString(),
      uri: this.nodeUri,
    };
  }

  static readAll(store: IndexedFormula) {
    return ModelRdfReader._readAll<Collection>(
      node => new CollectionRdfReader(node, store),
      store,
      PARADICMS.Collection
    );
  }
}
