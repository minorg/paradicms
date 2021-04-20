import {ModelRdfReader} from "./ModelRdfReader";
import {
  Object,
  Property,
  PropertyDefinition,
  RightsStatement,
} from "@paradicms/models";
import {DCTERMS, PARADICMS} from "./vocabularies";
import {IndexedFormula, Literal} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {ModelNode} from "./ModelNode";
import {PropertyValue} from "@paradicms/models/dist/PropertyValue";
import {LiteralWrapper} from "./LiteralWrapper";
import {PropertyDefinitionRdfReader} from "./PropertyDefinitionRdfReader";
import {RightsStatementRdfReader} from "./RightsStatementRdfReader";

export class ObjectRdfReader extends ModelRdfReader<Object> {
  constructor(
    node: ModelNode,
    private readonly propertyDefinitionsByUri: {
      [index: string]: PropertyDefinition;
    },
    private readonly rightsStatements: readonly RightsStatement[],
    store: IndexedFormula
  ) {
    super(node, store);
  }

  read(): Object {
    const properties: Property[] = [];
    // The number of properties per object is likely less than the number of property definitions available,
    // so loop on available properties (i.e., statements) rather than making a query per property definition.
    const nodeStatements = this.store.match(this.node);
    for (const nodeStatement of nodeStatements) {
      if (nodeStatement.predicate.termType !== "NamedNode") {
        continue;
      }
      const propertyDefinition = this.propertyDefinitionsByUri[
        nodeStatement.predicate.value
      ];
      if (!propertyDefinition) {
        continue;
      }
      if (nodeStatement.object.termType !== "Literal") {
        continue;
      }
      const literal = new LiteralWrapper(nodeStatement.object as Literal);
      let value: PropertyValue;
      if (literal.isBoolean()) {
        value = literal.toBoolean();
      } else if (literal.isInteger()) {
        value = literal.toInteger();
      } else if (literal.isString()) {
        value = literal.toString();
      } else {
        console.warn(
          "unknown literal datatype",
          literal.literal.datatype,
          "for property",
          propertyDefinition.uri
        );
        continue;
      }

      properties.push({
        uri: propertyDefinition.uri,
        value,
      });
    }

    return this.deleteUndefined({
      abstract: this.readOptionalLiteral(DCTERMS.abstract)?.toString(),
      collectionUris: this.readAllParentNamedNodes(PARADICMS.collection).map(
        node => node.value
      ),
      institutionUri: this.readRequiredParentNamedNode(PARADICMS.institution)
        .value,
      properties,
      rights: new RightsRdfReader(
        this.node,
        this.rightsStatements,
        this.store,
        nodeStatements
      ).read(),
      title: this.readRequiredLiteral(DCTERMS.title).toString(),
      uri: this.nodeUri,
    });
  }

  static readAll(store: IndexedFormula) {
    const propertyDefinitionsByUri = PropertyDefinitionRdfReader.readAll(
      store
    ).reduce((propertyDefinitionsByUri, propertyDefinition) => {
      propertyDefinitionsByUri[propertyDefinition.uri] = propertyDefinition;
      return propertyDefinitionsByUri;
    }, {} as {[index: string]: PropertyDefinition});

    const rightsStatements = RightsStatementRdfReader.readAll(store);

    return ModelRdfReader._readAll<Object>(
      node =>
        new ObjectRdfReader(
          node,
          propertyDefinitionsByUri,
          rightsStatements,
          store
        ),
      store,
      PARADICMS.Object
    );
  }
}
