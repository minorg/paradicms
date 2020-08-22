import * as React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Card,
  CardContent,
  CardHeader,
  Grid,
  List,
  ListItem,
  ListItemText,
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableRow,
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import {Image, Images, Institution, JoinedObject} from "@paradicms/models";
import {RightsTable} from "./RightsTable";

const useStyles = makeStyles(theme => ({
  expansionPanelText: {
    fontSize: "x-small",
    maxWidth: "32em",
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

  const descriptions = (object.properties ?? [])
    .filter(property => property.key == "description")
    .map(property => property.value);

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
      <CardHeader title={renderObjectLink(object, <>{object.title}</>)} />
      <CardContent>
        <Grid container direction="column" spacing={2}>
          {thumbnail ? (
            <Grid item>
              <div style={{height: 200, width: 200}}>
                <figure className="figure text-center w-100">
                  {renderObjectLink(
                    object,
                    <img className="figure-img rounded" src={thumbnail.uri} />
                  )}
                </figure>
              </div>
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
          {descriptions.length > 0 ? (
            <Grid item>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  Description
                </AccordionSummary>
                <AccordionDetails className={classes.expansionPanelText}>
                  {descriptions.length === 1 ? (
                    <span>{descriptions[0]}</span>
                  ) : (
                    <List>
                      {descriptions.map((description, descriptionIndex) => (
                        <ListItem key={descriptionIndex}>
                          <ListItemText>{description}</ListItemText>
                        </ListItem>
                      ))}
                    </List>
                  )}
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
                  <RightsTable rights={object.rights} />
                </AccordionDetails>
              </Accordion>
            </Grid>
          ) : null}
        </Grid>
      </CardContent>
    </Card>
  );
};
