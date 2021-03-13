import {Literal as rdflibLiteral} from "rdflib";
import {ModelRdfReader} from "./ModelRdfReader";
import {Rights, RightsValue} from "@paradicms/models";
import {DCTERMS} from "./vocabularies";
import {NamedNode} from "rdflib/lib/tf-types";
import {LiteralWrapper} from "./LiteralWrapper";

export class RightsRdfReader extends ModelRdfReader<Rights | undefined> {
  read(): Rights | undefined {
    const creator = this.readRightsValue(DCTERMS.creator);
    const holder = this.readRightsValue(DCTERMS.rightsHolder);
    const license = this.readRightsValue(DCTERMS.license);
    const statement = this.readRightsValue(DCTERMS.rights);
    if (creator || holder || license || statement) {
      return this.deleteUndefined({
        creator,
        holder,
        license,
        statement,
      });
    } else {
      return undefined;
    }
  }

  private readRightsValue(property: NamedNode): RightsValue | undefined {
    let text: string | undefined;
    let uri: string | undefined;

    const nodes = this.store.each(this.node, property, undefined);
    if (nodes.length === 0) {
      return undefined;
    }

    for (const node of nodes) {
      switch (node.termType) {
        case "Literal":
          const literal = new LiteralWrapper(node as rdflibLiteral);
          if (literal.isString()) {
            text = literal.toString();
          }
          break;
        case "NamedNode":
          uri = node.value;
          break;
      }
    }

    if (text || uri) {
      return {text, uri};
    } else {
      return undefined;
    }
  }
}
