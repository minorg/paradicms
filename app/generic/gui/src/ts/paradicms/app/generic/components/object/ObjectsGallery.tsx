import * as React from "react";
import { ObjectCard } from "paradicms/app/generic/components/object/ObjectCard";
import { ObjectSummary } from "paradicms/app/generic/models/object/ObjectSummary";
import { Grid } from "@material-ui/core";
import { Pagination } from "@material-ui/lab";

export const ObjectsGallery: React.FunctionComponent<{
  currentPage: number;
  maxPage: number;
  objects: ObjectSummary[];
  onPageRequest: (page: number) => void;
}> = ({
  currentPage,
  maxPage,
  objects,
  onPageRequest
}) => (
  <Grid container direction="column" spacing={4}>
    <Grid item>
      <Grid container>
        {objects.map(object => (
          <Grid item key={object.uri}>
            <ObjectCard object={object} />
          </Grid>
        ))}
      </Grid>
    </Grid>
    {maxPage > 1 ?
      <Grid item style={{alignSelf: "center"}}>
        <Pagination count={maxPage} page={currentPage} onChange={(event, value) => onPageRequest(value)}/>
      </Grid>
      : null}
  </Grid>
);
