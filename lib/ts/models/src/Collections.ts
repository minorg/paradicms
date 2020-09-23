import {Collection} from "./Collection";
import {Institution} from "./Institution";
import {Object} from "./Object";
import {Image} from "./Image";
import {JoinedCollection} from "JoinedCollection";

export class Collections {
  static join(kwds: {
    collections: readonly Collection[];
    imagesByDepictsUri: {[index: string]: readonly Image[]};
    institutionsByUri: {[index: string]: Institution};
    objectsByCollectionUri: {[index: string]: readonly Object[]};
  }): readonly JoinedCollection[] {
    const {
      collections,
      imagesByDepictsUri,
      institutionsByUri,
      objectsByCollectionUri,
    } = kwds;
    return collections.map(collection => {
      let collectionImages = imagesByDepictsUri[collection.uri];
      if (!collectionImages) {
        const collectionObjects = objectsByCollectionUri[collection.uri];
        if (collectionObjects) {
          for (const object of collectionObjects) {
            const objectImages = imagesByDepictsUri[object.uri];
            if (objectImages) {
              // Use the images of the first object with images as the collection's images
              collectionImages = objectImages;
              break;
            }
          }
        }
      }

      const institution = institutionsByUri[collection.institutionUri];
      if (!institution) {
        throw new EvalError(
          "unable to resolve institution " + collection.institutionUri
        );
      }

      return {
        collections,
        images: collectionImages ?? [],
        institution,
        title: collection.title,
        uri: collection.uri,
      };
    });
  }
}
