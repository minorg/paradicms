import * as React from "react";
import {Grid} from "@material-ui/core";
import {JoinedCollection} from "@paradicms/models";
import {CollectionCard} from "./CollectionCard";

export const CollectionsGallery: React.FunctionComponent<{
  collections: readonly JoinedCollection[];
  renderCollectionLink: (
    collection: JoinedCollection,
    children: React.ReactNode
  ) => React.ReactNode;
}> = ({collections, renderCollectionLink}) => (
  <Grid container direction="column" spacing={4}>
    <Grid item>
      <Grid container spacing={8}>
        {collections.map(collection => (
          <Grid item key={collection.uri}>
            <CollectionCard
              collection={collection}
              renderCollectionLink={renderCollectionLink}
            />
          </Grid>
        ))}
      </Grid>
    </Grid>
  </Grid>
);
