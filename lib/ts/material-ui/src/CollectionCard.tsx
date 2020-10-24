import * as React from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  makeStyles,
} from "@material-ui/core";
import {Images} from "@paradicms/models";
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

  const thumbnail = Images.selectThumbnail({
    images: collection.images,
    targetDimensions: {height: 200, width: 200},
  });

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
                      : Images.placeholderUrl({
                          dimensions: {height: 200, width: 200},
                          text: "Missing thumbnail",
                        })
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
