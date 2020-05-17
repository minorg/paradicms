import * as React from "react";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { Link } from "react-router-dom";
import { ObjectSummary } from "paradicms/app/generic/models/object/ObjectSummary";
import {
  Card,
  CardContent,
  CardHeader,
  ExpansionPanel,
  ExpansionPanelDetails,
  ExpansionPanelSummary,
  Grid,
  makeStyles
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";

const useStyles = makeStyles((theme) => ({
  expansionPanelText: {
    fontSize: "x-small",
    maxWidth: "32em"
  }
}));

export const ObjectCard: React.FunctionComponent<{object: ObjectSummary}> = ({
  object,
}) => {
  const objectHref = Hrefs.object({
    collectionUri: object.collectionUri,
    institutionUri: object.institutionUri,
    objectUri: object.uri,
  });

  const classes = useStyles();

  return (
    <Card>
      <CardHeader component="a" href={objectHref} title={object.title}/>
      <CardContent>
        <Grid container direction="column" spacing={1}>
          <Grid item>
            {object.thumbnail ? (
              <div style={{height: 200, width: 200}}>
                <figure className="figure text-center w-100">
                  <Link to={objectHref}>
                    <img
                      className="figure-img rounded"
                      src={object.thumbnail.url}
                    />
                  </Link>
                </figure>
              </div>
            ) : null}
          </Grid>
        {object.institutionName ? (
          <Grid item>
            Institution:{" "}
            <Link to={Hrefs.institution(object.institutionUri)}>
              {object.institutionName}
            </Link>
          </Grid>
        ) : null}
        {object.collectionName ? (
          <Grid item>
            Collection:{" "}
            <Link to={Hrefs.collection(object)}>{object.collectionName}</Link>
          </Grid>
        ) : null}
        {object.description ? (
          <Grid item>
            <ExpansionPanel>
              <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>Description</ExpansionPanelSummary>
              <ExpansionPanelDetails className={classes.expansionPanelText}>{object.description}</ExpansionPanelDetails>
            </ExpansionPanel>
          </Grid>
        ) : null}
        {object.rights ? (
          <Grid item>
            <ExpansionPanel>
              <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>Rights</ExpansionPanelSummary>
              <ExpansionPanelDetails className={classes.expansionPanelText}>{object.rights}</ExpansionPanelDetails>
            </ExpansionPanel>
          </Grid>
        ) : null}
        </Grid>
      </CardContent>
    </Card>
  );
};

// <figure className="figure">
//     <a onClick={this.onToggleSelected}>
//         <img
//             className="figure-img rounded"
//             src={thumbnailImgSrc}
//             style={{ height: "200px", width: "200px" }}
//         />
//     </a>
//     {definition.image ? (
//         <figcaption className="figure-caption" style={{ fontSize: "xx-small" }}>
//             Image&nbsp;&copy;
//             <span>{definition.image.rights.author}</span>
//             <br />License:&nbsp;
//             <span ><WorksheetRightsLicenseComponent license={definition.image.rights.license}></WorksheetRightsLicenseComponent></span>
//         </figcaption>
//     ) : null}
// </figure>
// {definition.description ? (
//     <div className="card-text">
//         <a onClick={this.onToggleDescription} className="description-toggle">Description</a>
//         <div className="float-right">
//             <a onClick={this.onToggleDescription} className="description-toggle">
//                 <i className={classnames({ fas: true, "fa-chevron-down": this.state.descriptionShown, "fa-chevron-right": !this.state.descriptionShown })}></i>
//             </a>
//         </div>
//         <br />
//         <Collapse isOpen={this.state.descriptionShown}>
//             <WorksheetDescriptionComponent description={definition.description}></WorksheetDescriptionComponent>
//         </Collapse>
//     </div>
// ) : null}
