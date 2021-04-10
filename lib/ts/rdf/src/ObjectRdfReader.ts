import {ModelRdfReader} from "./ModelRdfReader";
import {Object, Property, PropertyDefinition} from "@paradicms/models";
import {DCTERMS, PARADICMS} from "./vocabularies";
import {IndexedFormula, Literal, NamedNode} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {ModelNode} from "ModelNode";

export class ObjectRdfReader extends ModelRdfReader<Object> {
  constructor(
    node: ModelNode,
    private readonly propertyDefinitions: readonly PropertyDefinition[],
    store: IndexedFormula
  ) {
    super(node, store);
  }

  read(): Object {
    const properties: Property[] = [];
    for (const propertyDefinition of this.propertyDefinitions) {
      for (const node of this.store.each(
        this.node,
        {termType: "NamedNode", value: propertyDefinition.uri} as NamedNode,
        undefined
      )) {
        if (node.termType !== "Literal") {
          continue;
        }
        properties.push({
          uri: propertyDefinition.uri,
          value: (node as Literal).value,
        });
      }
    }

    return this.deleteUndefined({
      abstract: this.readOptionalLiteral(DCTERMS.abstract)?.toString(),
      collectionUris: this.readAllParentNamedNodes(PARADICMS.collection).map(
        node => node.value
      ),
      institutionUri: this.readRequiredParentNamedNode(PARADICMS.institution)
        .value,
      properties,
      rights: new RightsRdfReader(this.node, this.store).read(),
      title: this.readRequiredLiteral(DCTERMS.title).toString(),
      uri: this.nodeUri,
    });
  }

  static readAll(
    propertyDefinitions: readonly PropertyDefinition[],
    store: IndexedFormula
  ) {
    return ModelRdfReader._readAll<Object>(
      node => new ObjectRdfReader(node, propertyDefinitions, store),
      store,
      PARADICMS.Object
    );
  }
}
