import lunr, {Index} from "lunr";
import {Object, PropertyDefinition} from "@paradicms/models";
import {encodeFileName} from "@paradicms/base";

export class ObjectIndex {
  private readonly index: Index;
  private readonly objectsByUri: {[index: string]: Object};

  constructor(
    objects: readonly Object[],
    propertyDefinitions: readonly PropertyDefinition[]
  ) {
    const fieldsByPropertyDefinitionUri = propertyDefinitions.reduce(
      (fieldsByPropertyDefinitionUri, propertyDefinition) => {
        if (propertyDefinition.fullTextSearchable) {
          fieldsByPropertyDefinitionUri[propertyDefinition.uri] = {
            propertyDefinition,
            name: encodeFileName(propertyDefinition.uri),
          };
        }
        return fieldsByPropertyDefinitionUri;
      },
      {} as {
        [index: string]: {
          name: string;
          propertyDefinition: PropertyDefinition;
        };
      }
    );

    const objectsByUri: {[index: string]: Object} = {};
    this.objectsByUri = objectsByUri;

    this.index = lunr(function() {
      this.field("abstract");
      this.field("title");
      for (const field of Object.values(fieldsByPropertyDefinitionUri)) {
        this.field(field.name);
      }
      this.ref("uri");

      for (const object of objects) {
        const doc: any = {title: object.title, uri: object.uri};
        if (object.abstract) {
          doc.abstract = object.abstract;
        }
        if (object.properties && object.properties.length > 0) {
          for (const objectProperty of object.properties) {
            const field =
              fieldsByPropertyDefinitionUri[
                objectProperty.propertyDefinitionUri
              ];
            if (!field) {
              continue;
            }
            doc[field.name] = objectProperty.value;
          }
        }
        this.add(doc);
        objectsByUri[object.uri] = object;
      }
    });
  }

  search(query: string): readonly Object[] {
    return this.index.search(query).map(({ref}) => this.objectsByUri[ref]);
  }
}
