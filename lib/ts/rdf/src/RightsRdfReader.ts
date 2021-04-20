import {IndexedFormula, Literal as rdflibLiteral} from "rdflib";
import {ModelRdfReader} from "./ModelRdfReader";
import {Rights, RightsStatement, RightsValue} from "@paradicms/models";
import {DCTERMS} from "./vocabularies";
import {NamedNode, Quad} from "rdflib/lib/tf-types";
import {LiteralWrapper} from "./LiteralWrapper";
import {ModelNode} from "ModelNode";

export class RightsRdfReader extends ModelRdfReader<Rights | undefined> {
  private readonly nodeStatementsByPredicateUri: {
    [index: string]: readonly Quad[];
  };

  constructor(
    node: ModelNode,
    private readonly rightsStatements: readonly RightsStatement[],
    store: IndexedFormula,
    nodeStatements?: readonly Quad[]
  ) {
    super(node, store);

    if (!nodeStatements) {
      nodeStatements = this.store.match(this.node);
    }

    // Cache the node's statements from the store rather than doing multiple .each queries for each predicate
    this.nodeStatementsByPredicateUri = nodeStatements.reduce(
      (nodeStatementsByPredicateUri, statement) => {
        if (statement.predicate.termType === "NamedNode") {
          const existing =
            nodeStatementsByPredicateUri[statement.predicate.value];
          if (existing) {
            nodeStatementsByPredicateUri[
              statement.predicate.value
            ] = existing.concat(statement);
          } else {
            nodeStatementsByPredicateUri[statement.predicate.value] = [
              statement,
            ];
          }
        }
        return nodeStatementsByPredicateUri;
      },
      {} as {[index: string]: readonly Quad[]}
    );
  }

  read(): Rights | undefined {
    const creator = this.readRightsValue(DCTERMS.creator);

    const holder = this.readRightsValue(DCTERMS.rightsHolder);

    const license = this.readRightsValue(DCTERMS.license);

    const statement = this.readRightsValue(
      DCTERMS.rights,
      this.rightsStatements.reduce((defaultTextsByUri, rightsStatement) => {
        defaultTextsByUri[rightsStatement.uri] = rightsStatement.prefLabel;
        return defaultTextsByUri;
      }, {} as {[index: string]: string})
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

    // const nodes = this.store.each(this.node, property, undefined);
    // if (nodes.length === 0) {
    //   return undefined;
    // }

    const nodeStatements = this.nodeStatementsByPredicateUri[property.value];
    if (!nodeStatements) {
      return undefined;
    }

    for (const nodeStatement of nodeStatements) {
      const object = nodeStatement.object;

      switch (object.termType) {
        case "Literal":
          const literal = new LiteralWrapper(object as rdflibLiteral);
          if (literal.isString()) {
            text = literal.toString().trim();
            if (text.length === 0) {
              text = undefined;
            }
          }
          break;
        case "NamedNode":
          uri = object.value;
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
      if (!text || text.length === 0) {
        text = uri;
      }
    }

    if (!text || text.length === 0) {
      throw new EvalError();
    }

    return {text, uri};
  }
}
