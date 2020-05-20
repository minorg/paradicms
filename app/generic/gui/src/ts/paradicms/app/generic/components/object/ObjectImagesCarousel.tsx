import * as React from "react";
import ImageZoom from "react-medium-image-zoom";
import { ObjectOverviewQuery_objectByUri_images } from "paradicms/app/generic/api/queries/types/ObjectOverviewQuery";
import Carousel from "react-material-ui-carousel";

export const ObjectImagesCarousel: React.FunctionComponent<{
  images: ObjectOverviewQuery_objectByUri_images[];
}> = ({images}) => {
  return (
    <Carousel autoPlay={false}>
      {images.map(image =>
        <ImageZoom
          image={{
            className: "img",
            src: image.thumbnail
              ? image.thumbnail.url
              : "https://place-hold.it/600x600?text=Missing%20thumbnail",
            style: {
              maxHeight: 600,
              maxWidth: 600,
            }
          }}
          key={image.original.url}
          zoomImage={{
            className: "img--zoomed",
            src: image.original.url,
            style: image.original.exactDimensions ? image.original.exactDimensions : undefined
          }}
        />
      )}
    </Carousel>
  );
};
