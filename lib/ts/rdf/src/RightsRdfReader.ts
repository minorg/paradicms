import {Literal as rdflibLiteral} from "rdflib";
import {ModelRdfReader} from "./ModelRdfReader";
import {Rights, RightsValue} from "@paradicms/models";
import {DCTERMS} from "./vocabularies";
import {NamedNode} from "rdflib/lib/tf-types";
import {LiteralWrapper} from "./LiteralWrapper";
import {RightsStatementRdfReader} from "RightsStatementRdfReader";

export class RightsRdfReader extends ModelRdfReader<Rights | undefined> {
  read(): Rights | undefined {
    const creator = this.readRightsValue(DCTERMS.creator);

    const holder = this.readRightsValue(DCTERMS.rightsHolder);

    const license = this.readRightsValue(DCTERMS.license);

    const statement = this.readRightsValue(
      DCTERMS.rights,
      RightsStatementRdfReader.readAll(this.store).reduce(
        (defaultTextsByUri, rightsStatement) => {
          defaultTextsByUri[rightsStatement.uri] = rightsStatement.prefLabel;
          return defaultTextsByUri;
        },
        {} as {[index: string]: string}
      )
    );

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

  private readRightsValue(
    property: NamedNode,
    defaultTextsByUri?: {[index: string]: string}
  ): RightsValue | undefined {
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

    if (!text && !uri) {
      return undefined;
    }

    if (!text && uri) {
      if (defaultTextsByUri) {
        text = defaultTextsByUri[uri];
      }
      if (!text) {
        text = uri;
      }
    }

    if (!text) {
      throw new EvalError();
    }

    return {text, uri};
  }
}
