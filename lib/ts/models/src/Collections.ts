import {Collection} from "./Collection";
import {Institution} from "./Institution";
import {Object} from "./Object";
import {Image} from "./Image";
import {JoinedCollection} from "./JoinedCollection";

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
      const institution = institutionsByUri[collection.institutionUri];
      if (!institution) {
        throw new EvalError(
          "unable to resolve institution " + collection.institutionUri
        );
      }

      return {
        collections,
        images: Collections.selectCollectionImages({
          collection,
          imagesByDepictsUri,
          objectsByCollectionUri,
        }),
        institution,
        title: collection.title,
        uri: collection.uri,
      };
    });
  }

  static selectCollectionImages(kwds: {
    collection: Collection;
    imagesByDepictsUri: {[index: string]: readonly Image[]};
    objectsByCollectionUri: {[index: string]: readonly Object[]};
  }): readonly Image[] {
    const {collection, imagesByDepictsUri, objectsByCollectionUri} = kwds;
    let collectionImages = imagesByDepictsUri[collection.uri];
    if (collectionImages) {
      return collectionImages;
    }

    const collectionObjects = objectsByCollectionUri[collection.uri];
    if (!collectionObjects) {
      return [];
    }

    for (const object of collectionObjects) {
      const objectImages = imagesByDepictsUri[object.uri];
      if (objectImages) {
        // Use the images of the first object with images as the collection's images
        return objectImages;
      }
    }

    return [];
  }
}
