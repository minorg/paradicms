import {ModelRdfReader} from "./ModelRdfReader";
import {Image, ImageDimensions, RightsStatement} from "@paradicms/models";
import {EXIF, FOAF, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {NamedNode} from "rdflib/lib/tf-types";
import {RdfReaderException} from "./RdfReaderException";
import {ModelNode} from "./ModelNode";
import {RightsStatementRdfReader} from "./RightsStatementRdfReader";

export class ImageRdfReader extends ModelRdfReader<Image> {
  constructor(
    node: ModelNode,
    private readonly rightsStatements: readonly RightsStatement[],
    store: IndexedFormula
  ) {
    super(node, store);
  }

  read(): Image {
    return this.deleteUndefined({
      depictsUri: this.readRequiredParentNamedNode(FOAF.depicts).value,
      exactDimensions: this.readImageDimensions(EXIF.height, EXIF.width),
      institutionUri: this.readRequiredParentNamedNode(PARADICMS.institution)
        .value,
      maxDimensions: this.readImageDimensions(
        PARADICMS.imageMaxHeight,
        PARADICMS.imageMaxWidth
      ),
      originalImageUri: this.store.any(undefined, FOAF.thumbnail, this.node)
        ?.value,
      rights: new RightsRdfReader(
        this.node,
        this.rightsStatements,
        this.store
      ).read(),
      uri: this.nodeUri,
    });
  }

  private readImageDimensions(
    heightProperty: NamedNode,
    widthProperty: NamedNode
  ): ImageDimensions | undefined {
    const heightLiteral = this.readOptionalLiteral(heightProperty);
    const widthLiteral = this.readOptionalLiteral(widthProperty);

    if (heightLiteral) {
      if (widthLiteral) {
        return {
          height: heightLiteral.toInteger(),
          width: widthLiteral.toInteger(),
        };
      } else {
        throw new RdfReaderException(
          `image ${this.nodeUri} has a ${heightProperty.value} but not a ${widthProperty.value}`
        );
      }
    } else if (widthLiteral) {
      throw new RdfReaderException(
        `image ${this.nodeUri} has a ${widthProperty.value} but not a ${heightProperty.value}`
      );
    } else {
      return undefined;
    }
  }

  static readAll(store: IndexedFormula) {
    const rightsStatements = RightsStatementRdfReader.readAll(store);
    return ModelRdfReader._readAll<Image>(
      node => new ImageRdfReader(node, rightsStatements, store),
      store,
      PARADICMS.Image
    );
  }
}
