import * as React from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  makeStyles,
} from "@material-ui/core";
import {Images} from "@paradicms/models";
import {JoinedInstitution} from "@paradicms/models";

const useStyles = makeStyles(theme => ({
  thumbnailImg: {
    maxHeight: "200px",
    maxWidth: "200px",
  },
  root: {
    width: "400px",
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
    <Card className={classes.root}>
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
                      : Images.placeholderUrl({
                          dimensions: {height: 200, width: 200},
                          text: "Missing thumbnail",
                        })
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
