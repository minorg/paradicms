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
import {RightsValueLink} from "./RightsValueLink";

const useStyles = makeStyles(theme => ({
  expansionPanelText: {
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

  let thumbnail: Image | undefined;
  const objectImagesByOriginalImageUri = Images.indexByOriginalImageUri(
    object.images
  );
  for (const originalImageUri of Object.keys(objectImagesByOriginalImageUri)) {
    thumbnail = Images.selectThumbnail({
      images: objectImagesByOriginalImageUri[originalImageUri],
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
        title={renderObjectLink(object, <>{object.title}</>)}
      />
      <CardContent>
        <Grid container direction="column" spacing={2}>
          {thumbnail ? (
            <Grid item container alignItems="center" justify="center">
              <Grid item>
                {renderObjectLink(
                  object,
                  <img
                    className={classes.thumbnailImg}
                    src={thumbnail.uri}
                    title={object.title}
                  />
                )}
              </Grid>
            </Grid>
          ) : null}
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
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  Summary
                </AccordionSummary>
                <AccordionDetails className={classes.expansionPanelText}>
                  {object.abstract}
                </AccordionDetails>
              </Accordion>
            </Grid>
          ) : null}
          {object.rights ? (
            <Grid item>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  Rights
                </AccordionSummary>
                <AccordionDetails className={classes.expansionPanelText}>
                  <Table className={classes.rightsTable}>
                    {object.rights.statement ? (
                      <TableRow>
                        <TableCell className={classes.rightsTableCell}>
                          Statement
                        </TableCell>
                        <TableCell className={classes.rightsTableCell}>
                          <RightsValueLink value={object.rights.statement} />
                        </TableCell>
                      </TableRow>
                    ) : null}
                    {object.rights.holder ? (
                      <TableRow>
                        <TableCell className={classes.rightsTableCell}>
                          Holder
                        </TableCell>
                        <TableCell className={classes.rightsTableCell}>
                          <RightsValueLink value={object.rights.holder} />
                        </TableCell>
                      </TableRow>
                    ) : null}
                    {object.rights.license ? (
                      <TableRow>
                        <TableCell className={classes.rightsTableCell}>
                          License
                        </TableCell>
                        <TableCell className={classes.rightsTableCell}>
                          <RightsValueLink value={object.rights.license} />
                        </TableCell>
                      </TableRow>
                    ) : null}
                  </Table>
                </AccordionDetails>
              </Accordion>
            </Grid>
          ) : null}
        </Grid>
      </CardContent>
    </Card>
  );
};
