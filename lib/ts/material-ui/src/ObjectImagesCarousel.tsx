import * as React from "react";
import ImageZoom from "react-medium-image-zoom";
import Carousel from "react-material-ui-carousel";
import {Image, Images} from "@paradicms/models";
import {AccordionSummary, AccordionDetails, Grid} from "@material-ui/core";
import {Accordion} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import {RightsTable} from "./RightsTable";

export const ObjectImagesCarousel: React.FunctionComponent<{
  images: readonly Image[];
}> = ({images}) => {
  const imagesByOriginalImageUri = Images.indexByOriginalImageUri(images);
  return (
    <Carousel autoPlay={false}>
      <Grid container alignContent="center" justify="center">
        {Object.keys(imagesByOriginalImageUri).map(originalImageUri => {
          const images = imagesByOriginalImageUri[originalImageUri];
          const originalImage = images.find(
            image => image.uri === originalImageUri
          );
          const thumbnail = Images.selectThumbnail({
            images,
            maxDimensions: {height: 600, width: 600},
          });
          return (
            <Grid item key={originalImageUri}>
              <ImageZoom
                image={{
                  className: "img",
                  src: thumbnail
                    ? thumbnail.uri
                    : "https://place-hold.it/600x600?text=Missing%20thumbnail",
                  style: {
                    maxHeight: 600,
                    maxWidth: 600,
                  },
                }}
                zoomImage={{
                  className: "img--zoomed",
                  src: originalImageUri,
                  style: originalImage?.exactDimensions ?? undefined,
                }}
              />
              {originalImage && originalImage.rights ? (
                <Accordion>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    Image rights
                  </AccordionSummary>
                  <AccordionDetails>
                    <RightsTable rights={originalImage.rights} />
                  </AccordionDetails>
                </Accordion>
              ) : null}
            </Grid>
          );
        })}
      </Grid>
    </Carousel>
  );
};
