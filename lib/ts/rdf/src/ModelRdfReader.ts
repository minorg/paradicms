import {IndexedFormula, Literal as rdflibLiteral} from "rdflib";
import {NamedNode} from "rdflib/lib/tf-types";
import {RDF} from "./vocabularies";
import {ModelNode} from "./ModelNode";
import {LiteralWrapper} from "./LiteralWrapper";

export abstract class ModelRdfReader<ModelT> {
  protected constructor(
    protected readonly node: ModelNode,
    protected readonly store: IndexedFormula
  ) {}

  get nodeUri(): string {
    switch (this.node.termType) {
      case "BlankNode":
        throw new EvalError("tried to get URI of blank node");
      case "NamedNode":
        return this.node.value;
    }
  }

  protected static _readAll<ModelT>(
    readerFactory: (node: ModelNode) => ModelRdfReader<ModelT>,
    store: IndexedFormula,
    type: NamedNode
  ): ModelT[] {
    return store.each(undefined, RDF.type, type).map(node => {
      switch (node.termType) {
        case "BlankNode":
        case "NamedNode":
          break;
        default:
          throw new EvalError(
            `expected BlankNode or NamedNode, actual ${node.termType}`
          );
      }
      return readerFactory(node as ModelNode).read();
    });
  }

  protected readOptionalLiteral(
    property: NamedNode
  ): LiteralWrapper | undefined {
    const nodes = this.store.each(this.node, property, undefined);
    if (nodes.length === 0) {
      return undefined;
    }
    for (const node of nodes) {
      if (node.termType === "Literal") {
        return new LiteralWrapper(node as rdflibLiteral);
      }
    }
    return undefined;
  }

  protected readRequiredLiteral(property: NamedNode): LiteralWrapper {
    const literal = this.readOptionalLiteral(property);
    if (!literal) {
      throw new EvalError("missing required property " + property.value);
    }
    if (literal.isBlank()) {
      throw new EvalError("required property is blank " + property.value);
    }
    return literal;
  }

  abstract read(): ModelT;
}
