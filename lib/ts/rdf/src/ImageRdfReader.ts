import {ModelRdfReader} from "./ModelRdfReader";
import {Image, ImageDimensions} from "@paradicms/models";
import {EXIF, FOAF, PARADICMS} from "./vocabularies";
import {IndexedFormula} from "rdflib";
import {RightsRdfReader} from "./RightsRdfReader";
import {NamedNode} from "rdflib/lib/tf-types";
import {RdfReaderException} from "./RdfReaderException";

export class ImageRdfReader extends ModelRdfReader<Image> {
  read(): Image {
    return {
      depictsUri: this.readParentNamedNode(FOAF.depicts).value,
      exactDimensions: this.readImageDimensions(EXIF.height, EXIF.width),
      institutionUri: this.readParentNamedNode(PARADICMS.institution).value,
      maxDimensions: this.readImageDimensions(
        PARADICMS.imageMaxHeight,
        PARADICMS.imageMaxWidth
      ),
      originalImageUri: this.store.any(undefined, FOAF.thumbnail, this.node)
        ?.value,
      rights: new RightsRdfReader(this.node, this.store).read(),
      uri: this.nodeUri,
    };
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
    return ModelRdfReader._readAll<Image>(
      node => new ImageRdfReader(node, store),
      store,
      PARADICMS.Image
    );
  }
}
