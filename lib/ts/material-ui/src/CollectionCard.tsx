import * as React from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  makeStyles,
} from "@material-ui/core";
import {Image, Images} from "@paradicms/models";
import {JoinedCollection} from "@paradicms/models/dist/JoinedCollection";

const useStyles = makeStyles(theme => ({
  thumbnailImg: {
    maxHeight: "200px",
    maxWidth: "200px",
  },
  title: {
    textAlign: "center",
  },
}));

export const CollectionCard: React.FunctionComponent<{
  collection: JoinedCollection;
  renderCollectionLink: (
    collection: JoinedCollection,
    children: React.ReactNode
  ) => React.ReactNode;
}> = ({collection, renderCollectionLink}) => {
  const classes = useStyles();

  let thumbnail: Image | undefined;
  const collectionImagesByOriginalImageUri = Images.indexByOriginalImageUri(
    collection.images
  );
  for (const originalImageUri of Object.keys(
    collectionImagesByOriginalImageUri
  )) {
    thumbnail = Images.selectThumbnail({
      images: collectionImagesByOriginalImageUri[originalImageUri],
      maxDimensions: {height: 200, width: 200},
    });
    if (thumbnail) {
      break;
    }
  }

  return (
    <Card>
      <CardHeader
        className={classes.title}
        title={renderCollectionLink(collection, <>{collection.title}</>)}
      />
      <CardContent>
        <Grid container direction="column" spacing={2}>
          <Grid item container alignItems="center" justify="center">
            <Grid item>
              {renderCollectionLink(
                collection,
                <img
                  className={classes.thumbnailImg}
                  src={
                    thumbnail
                      ? thumbnail.uri
                      : "https://place-hold.it/200x200?text=Missing%20thumbnail"
                  }
                  title={collection.title}
                />
              )}
            </Grid>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};
