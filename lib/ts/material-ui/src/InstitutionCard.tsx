import * as React from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  makeStyles,
} from "@material-ui/core";
import {Images} from "@paradicms/models";
import {JoinedInstitution} from "@paradicms/models/dist/JoinedInstitution";

const useStyles = makeStyles(theme => ({
  thumbnailImg: {
    maxHeight: "200px",
    maxWidth: "200px",
  },
  title: {
    textAlign: "center",
  },
}));

export const InstitutionCard: React.FunctionComponent<{
  institution: JoinedInstitution;
  renderInstitutionLink: (
    institution: JoinedInstitution,
    children: React.ReactNode
  ) => React.ReactNode;
}> = ({institution, renderInstitutionLink}) => {
  const classes = useStyles();

  const thumbnail = Images.selectThumbnail({
    images: institution.images,
    targetDimensions: {height: 200, width: 200},
  });

  return (
    <Card>
      <CardHeader
        className={classes.title}
        title={renderInstitutionLink(institution, <>{institution.name}</>)}
      />
      <CardContent>
        <Grid container direction="column" spacing={2}>
          <Grid item container alignItems="center" justify="center">
            <Grid item>
              {renderInstitutionLink(
                institution,
                <img
                  className={classes.thumbnailImg}
                  src={
                    thumbnail
                      ? thumbnail.uri
                      : "https://place-hold.it/200x200?text=Missing%20thumbnail"
                  }
                  title={institution.name}
                />
              )}
            </Grid>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};
