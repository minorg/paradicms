import * as React from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
  List,
  ListItem,
  ListItemText,
  makeStyles,
  Table,
  TableCell,
  TableRow,
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import {Link} from "gatsby";
import {Hrefs} from "~/Hrefs";
import {JoinedObject} from "~/models/object/JoinedObject";
import {Images} from "~/models/image/Images";
import {RightsTable} from "~/components/rights/RightsTable";

const useStyles = makeStyles(theme => ({
  expansionPanelText: {
    fontSize: "x-small",
    maxWidth: "32em",
  },
}));

export const ObjectCard: React.FunctionComponent<{
  object: JoinedObject;
}> = ({object}) => {
  const classes = useStyles();

  const collection = object.collections[0];

  const objectHref = Hrefs.institution(object.institution)
    .collection(collection)
    .object(object);

  const thumbnail = Images.selectThumbnail({
    images: object.images,
    maxDimensions: {height: 200, width: 200},
  });

  return (
    <Card>
      <CardHeader component="a" href={objectHref} title={object.title} />
      <CardContent>
        <Grid container direction="column" spacing={2}>
          {thumbnail ? (
            <Grid item>
              <div style={{height: 200, width: 200}}>
                <figure className="figure text-center w-100">
                  <Link to={objectHref}>
                    <img className="figure-img rounded" src={thumbnail.uri} />
                  </Link>
                </figure>
              </div>
            </Grid>
          ) : null}
          <Grid item>
            <Table>
              <TableRow>
                <TableCell>
                  <strong>Institution</strong>
                </TableCell>
                <TableCell>
                  <Link to={Hrefs.institution(object.institution).home}>
                    {object.institution.name}
                  </Link>
                </TableCell>
              </TableRow>
              <TableRow>
                <TableCell>
                  <strong>Collection</strong>
                </TableCell>
                <TableCell>
                  <Link
                    to={
                      Hrefs.institution(object.institution).collection(
                        collection
                      ).home
                    }
                  >
                    {collection.title}
                  </Link>
                </TableCell>
              </TableRow>
            </Table>
          </Grid>
          {object.descriptions && object.descriptions.length > 0 ? (
            <Grid item>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  Description
                </AccordionSummary>
                <AccordionDetails className={classes.expansionPanelText}>
                  {object.descriptions.length === 1 ? (
                    <span>{object.descriptions[0]}</span>
                  ) : (
                    <List>
                      {object.descriptions.map(
                        (description, descriptionIndex) => (
                          <ListItem key={descriptionIndex}>
                            <ListItemText>{description}</ListItemText>
                          </ListItem>
                        )
                      )}
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
