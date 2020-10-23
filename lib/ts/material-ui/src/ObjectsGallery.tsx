import * as React from "react";
import {Grid} from "@material-ui/core";
import {Pagination} from "@material-ui/lab";
import {Institution, JoinedObject} from "@paradicms/models";
import {ObjectCard} from "./ObjectCard";

const OBJECTS_PER_PAGE = 10;

export const ObjectsGallery: React.FunctionComponent<{
  objects: readonly JoinedObject[];
  onChangePage: (page: number) => void;
  page: number; // From 0
  renderInstitutionLink?: (
    institution: Institution,
    children: React.ReactNode
  ) => React.ReactNode;
  renderObjectLink: (
    object: JoinedObject,
    children: React.ReactNode
  ) => React.ReactNode;
}> = ({
  objects,
  onChangePage,
  page,
  renderInstitutionLink,
  renderObjectLink,
}) => {
  const maxPage = Math.ceil(objects.length / OBJECTS_PER_PAGE) - 1;

  return (
    <Grid container direction="column" spacing={4}>
      <Grid item>
        <Grid container spacing={8}>
          {objects
            .slice(page * OBJECTS_PER_PAGE, (page + 1) * OBJECTS_PER_PAGE)
            .map(object => (
              <Grid item key={object.uri}>
                <ObjectCard
                  object={object}
                  renderInstitutionLink={renderInstitutionLink}
                  renderObjectLink={renderObjectLink}
                />
              </Grid>
            ))}
        </Grid>
      </Grid>
      {maxPage > 0 ? (
        <Grid item style={{alignSelf: "center"}}>
          <Pagination
            count={maxPage + 1}
            page={page + 1}
            onChange={(_, value) => onChangePage(value - 1)}
          />
        </Grid>
      ) : null}
    </Grid>
  );
};
