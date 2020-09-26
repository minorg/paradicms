import * as React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Card,
  CardContent,
  CardHeader,
  Grid,
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableRow,
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import {Image, Images, Institution, JoinedObject} from "@paradicms/models";
import {RightsTable} from "./RightsTable";
import {invariant} from "ts-invariant";

const useStyles = makeStyles(theme => ({
  accordionTitle: {
    fontSize: "smaller",
  },
  objectSummary: {
    fontSize: "x-small",
    maxWidth: "32em",
  },
  thumbnailImg: {
    maxHeight: "200px",
    maxWidth: "200px",
  },
  rightsTableCell: {
    fontSize: "x-small",
    padding: theme.spacing(1),
  },
  title: {
    textAlign: "center",
  },
}));

export const ObjectCard: React.FunctionComponent<{
  object: JoinedObject;
  renderInstitutionLink?: (
    institution: Institution,
    children: React.ReactNode
  ) => React.ReactNode;
  renderObjectLink: (
    object: JoinedObject,
    children: React.ReactNode
  ) => React.ReactNode;
}> = ({object, renderInstitutionLink, renderObjectLink}) => {
  const classes = useStyles();

  invariant(object.images != null, "object images must be set");

  let thumbnail: Image | undefined;
  const objectImagesByOriginalImageUri = Images.indexByOriginalImageUri(
    object.images
  );
  for (const originalImageUri of Object.keys(objectImagesByOriginalImageUri)) {
    thumbnail = Images.selectThumbnail({
      images: objectImagesByOriginalImageUri[originalImageUri],
      targetDimensions: {height: 200, width: 200},
    });
    if (thumbnail) {
      break;
    }
  }

  return (
    <Card>
      <CardHeader
        className={classes.title}
        title={renderObjectLink(object, <>{object.title}</>)}
      />
      <CardContent>
        <Grid container direction="column" spacing={2}>
          <Grid item container alignItems="center" justify="center">
            <Grid item>
              {renderObjectLink(
                object,
                <img
                  className={classes.thumbnailImg}
                  src={
                    thumbnail
                      ? thumbnail.uri
                      : "https://place-hold.it/200x200?text=Missing%20thumbnail"
                  }
                  title={object.title}
                />
              )}
            </Grid>
          </Grid>
          {renderInstitutionLink ? (
            <Grid item>
              <Table>
                <TableBody>
                  <TableRow>
                    <TableCell>
                      <strong>Institution</strong>
                    </TableCell>
                    <TableCell>
                      {renderInstitutionLink(
                        object.institution,
                        <span>{object.institution.name}</span>
                      )}
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </Grid>
          ) : null}
          {object.abstract ? (
            <Grid item>
              <Accordion>
                <AccordionSummary
                  className={classes.accordionTitle}
                  expandIcon={<ExpandMoreIcon />}
                >
                  Summary
                </AccordionSummary>
                <AccordionDetails className={classes.objectSummary}>
                  {object.abstract}
                </AccordionDetails>
              </Accordion>
            </Grid>
          ) : null}
          {thumbnail && thumbnail.rights ? (
            <Grid item>
              <Accordion>
                <AccordionSummary
                  className={classes.accordionTitle}
                  expandIcon={<ExpandMoreIcon />}
                >
                  Image rights
                </AccordionSummary>
                <AccordionDetails>
                  <RightsTable
                    cellClassName={classes.rightsTableCell}
                    rights={thumbnail.rights}
                  ></RightsTable>
                </AccordionDetails>
              </Accordion>
            </Grid>
          ) : null}
          {object.rights ? (
            <Grid item>
              <Accordion>
                <AccordionSummary
                  className={classes.accordionTitle}
                  expandIcon={<ExpandMoreIcon />}
                >
                  Metadata rights
                </AccordionSummary>
                <AccordionDetails>
                  <RightsTable
                    cellClassName={classes.rightsTableCell}
                    rights={object.rights}
                  ></RightsTable>
                </AccordionDetails>
              </Accordion>
            </Grid>
          ) : null}
        </Grid>
      </CardContent>
    </Card>
  );
};
