import * as React from "react";
import { useState } from "react";
import { Carousel, CarouselControl, CarouselIndicators, CarouselItem } from "reactstrap";
import ImageZoom from "react-medium-image-zoom";
import { ObjectOverviewQuery_objectByUri_images } from "paradicms/app/generic/api/queries/types/ObjectOverviewQuery";

export const ObjectImagesCarousel: React.FunctionComponent<{
  images: ObjectOverviewQuery_objectByUri_images[];
}> = ({images}) => {
  const [activeIndex, setActiveIndex] = useState(0);
  const [animating, setAnimating] = useState(false);

  const next = () => {
    if (animating) return;
    const nextIndex = activeIndex === images.length - 1 ? 0 : activeIndex + 1;
    setActiveIndex(nextIndex);
  };

  const previous = () => {
    if (animating) return;
    const nextIndex = activeIndex === 0 ? images.length - 1 : activeIndex - 1;
    setActiveIndex(nextIndex);
  };

  const goToIndex = (newIndex: number) => {
    if (animating) return;
    setActiveIndex(newIndex);
  };

  return (
    <Carousel activeIndex={activeIndex} next={next} previous={previous}>
      {images.length > 1 ? (
        <CarouselIndicators
          items={images}
          activeIndex={activeIndex}
          onClickHandler={goToIndex}
        />
      ) : null}
      {images.map(image => {
        return (
          <CarouselItem
            className="text-center"
            onExiting={() => setAnimating(true)}
            onExited={() => setAnimating(false)}
            key={image.original.url}
          >
            <ImageZoom
              image={{
                className: "img",
                src: image.thumbnail
                  ? image.thumbnail.url
                  : "https://place-hold.it/200x200?text=Missing%20thumbnail",
                style: {
                  height: 200,
                  width: 200,
                }
              }}
              zoomImage={{
                className: "img--zoomed",
                src: image.original.url,
                style: image.original.exactDimensions ? image.original.exactDimensions : undefined
              }}
            />
            {/*<CarouselCaption captionText={item.caption} captionHeader={item.caption}/>*/}
          </CarouselItem>
        );
      })}
      {images.length > 1 ? (
        <React.Fragment>
          <CarouselControl
            direction="prev"
            directionText="Previous"
            onClickHandler={previous}
          />
          <CarouselControl
            direction="next"
            directionText="Next"
            onClickHandler={next}
          />
        </React.Fragment>
      ) : null}
    </Carousel>
  );
};
