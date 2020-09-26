import {Image} from "./Image";
import {ImageDimensions} from "./ImageDimensions";

export class Images {
  static indexByDepictsUri(
    images: readonly Image[]
  ): {[index: string]: readonly Image[]} {
    return images.reduce((map, image) => {
      const existingImages = map[image.depictsUri];
      if (existingImages) {
        existingImages.push(image);
      } else {
        map[image.depictsUri] = [image];
      }
      return map;
    }, {} as {[index: string]: Image[]});
  }

  static indexByOriginalImageUri(
    images: readonly Image[]
  ): {[index: string]: readonly Image[]} {
    return images.reduce((map, image) => {
      const originalImageUri = image.originalImageUri ?? image.uri;
      const existingImages = map[originalImageUri];
      if (existingImages) {
        existingImages.push(image);
      } else {
        map[originalImageUri] = [image];
      }
      return map;
    }, {} as {[index: string]: Image[]});
  }

  static selectThumbnail(kwds: {
    images: readonly Image[];
    minDimensions?: ImageDimensions;
    maxDimensions?: ImageDimensions;
    targetDimensions: ImageDimensions;
  }): Image | undefined {
    const {images, minDimensions, maxDimensions, targetDimensions} = kwds;

    const candidateImages: {
      image: Image;
      imageDimensions: ImageDimensions;
    }[] = [];
    for (const image of images) {
      let imageDimensions: ImageDimensions;
      if (image.exactDimensions) {
        imageDimensions = image.exactDimensions;
      } else if (image.maxDimensions) {
        imageDimensions = image.maxDimensions;
      } else {
        continue;
      }

      if (maxDimensions) {
        if (imageDimensions.height > maxDimensions.height) {
          continue;
        }
        if (imageDimensions.width > maxDimensions.width) {
          continue;
        }
      }

      if (minDimensions) {
        if (imageDimensions.height < minDimensions.height) {
          continue;
        }
        if (imageDimensions.width < minDimensions.width) {
          continue;
        }
      }

      candidateImages.push({
        image,
        imageDimensions,
      });
    }

    if (candidateImages.length === 0) {
      // console.debug("no candidate images, returning undefined");
      return undefined;
    } else if (candidateImages.length === 1) {
      // console.debug("single candidate image");
      return candidateImages[0].image;
    }

    const contains = (
      leftDimensions: ImageDimensions,
      rightDimensions: ImageDimensions
    ) =>
      leftDimensions.width >= rightDimensions.width &&
      leftDimensions.height >= rightDimensions.height;

    // Sort images smallest to largest
    // An image A is considered larger than another image B if A's dimensions contain B's dimensions
    // Otherwise the images are considered equal.
    candidateImages.sort((left, right) => {
      if (contains(left.imageDimensions, right.imageDimensions)) {
        return 1;
      } else if (contains(right.imageDimensions, left.imageDimensions)) {
        return -1;
      } else {
        return 0;
      }
    });

    // Find the smallest image that contains the target dimensions.
    // We prefer to scale an image down instead of up.
    // This may lead to choosing very large images. In that case maxDimensions should be used to exclude very large images as candidates.
    for (const candidateImage of candidateImages) {
      if (contains(candidateImage.imageDimensions, targetDimensions)) {
        // console.debug(
        //   "choosing smallest candidate image that's larger than target dimensions"
        // );
        return candidateImage.image;
      }
    }

    // All candidate images are smaller than the target, return the largest of them
    // console.debug(
    //   "choosing largest candidate image that's smaller than target dimensions"
    // );
    return candidateImages[candidateImages.length - 1].image;
  }
}
