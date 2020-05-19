import * as React from "react";
import { InstitutionCollectionObjectOverview } from "paradicms/app/generic/components/frame/InstitutionCollectionObjectOverview";
import { useQuery } from "@apollo/react-hooks";
import {
  ObjectOverviewQuery,
  ObjectOverviewQueryVariables
} from "paradicms/app/generic/api/queries/types/ObjectOverviewQuery";
import * as ObjectOverviewQueryDocument from "paradicms/app/generic/api/queries/ObjectOverviewQuery.graphql";
import { ObjectImagesCarousel } from "paradicms/app/generic/components/object/ObjectImagesCarousel";
import { RightsTable } from "paradicms/app/generic/components/rights/RightsTable";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import { ApolloException } from "@paradicms/base";
import * as ReactLoader from "react-loader";
import { useParams } from "react-router-dom";
import { Card, CardContent, CardHeader, Grid, List, ListItemText } from "@material-ui/core";

// type Object = ObjectCardObject;

export const ObjectOverview: React.FunctionComponent = () => {
  const params = useParams<{
    collectionUri: string;
    institutionUri: string;
    objectUri: string;
  }>();

  const collectionUri = decodeURIComponent(params.collectionUri);
  const institutionUri = decodeURIComponent(params.institutionUri);
  const objectUri = decodeURIComponent(params.objectUri);

  const { data, error, loading } = useQuery<ObjectOverviewQuery, ObjectOverviewQueryVariables>(ObjectOverviewQueryDocument, {
    variables: {
      collectionUri, institutionUri, objectUri
    }
  });

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false}/>;
  } else if (!data) {
    throw new EvalError();
  }

  const nameValueTableRows = (name: string, values: string[]) =>
    values.map(value => (
      <tr key={value}>
        <td className="px-2">
          <strong>{name}</strong>
        </td>
        <td className="px-2">{value}</td>
      </tr>
    ));

  const listGroupSection = (id: string, title: string, values: string[]) =>
    values.length > 0 ? (
      <Grid item className={id + "-section section pb-4"}>
        <Card>
          <CardHeader title={title}/>
          <CardContent>
            <List>
              {values.map(value => (
                <ListItemText key={value}>{value}</ListItemText>
              ))}
            </List>
          </CardContent>
        </Card>
      </Grid>
    ) : null;

  const object_ = data.objectByUri;
  const rights = data.objectByUri.rights
    ? data.objectByUri.rights
    : data.collectionByUri.rights
      ? data.collectionByUri.rights
      : data.institutionByUri.rights;
  return (
    <InstitutionCollectionObjectOverview
      collectionName={data.collectionByUri.name}
      collectionUri={collectionUri}
      institutionName={data.institutionByUri.name}
      institutionUri={institutionUri}
      objectTitle={data.objectByUri.title}
      objectUri={objectUri}
      title={data.objectByUri.title}
    >
      <Grid container direction="column" spacing={2}>
        {object_.images.length > 0 ? (
          <Grid item className="section" id="carousel-section">
            <Card>
              <CardContent>
                <ObjectImagesCarousel images={object_.images}/>
              </CardContent>
            </Card>
          </Grid>
        ) : null}
        {listGroupSection("descriptions","Descriptions", object_.descriptions)}
        {object_.titles.length > 1 ||
        object_.alternativeTitles.length > 0 ||
        object_.titles[0] !== object_.title ? (
          <Grid item className="section" id="titles-section">
            <Card>
              <CardHeader title="Titles"/>
              <CardContent>
                <table>
                  <tbody>
                  {nameValueTableRows("Title", object_.titles)}
                  {nameValueTableRows(
                    "Alternative title",
                    object_.alternativeTitles
                  )}
                  </tbody>
                </table>
              </CardContent>
            </Card>
          </Grid>
        ) : null}
        {listGroupSection("identifiers", "Identifiers", object_.identifiers)}
        {listGroupSection("subjects","Subjects", object_.subjects)}
        {listGroupSection("types","Types", object_.types)}
        {object_.creators.length > 0 ||
        object_.provenances.length > 0 ||
        object_.publishers.length > 0 ||
        object_.sources.length > 0 ? (
          <Grid item className="section" id="provenance-section">
            <Card className="w-100">
              <CardHeader title="Provenance"/>
              <CardContent>
                <table className="table-bordered w-100">
                  <tbody>
                  {nameValueTableRows("Creator", object_.creators)}
                  {nameValueTableRows("Publisher", object_.publishers)}
                  {nameValueTableRows(
                    "Provenance",
                    object_.provenances
                  )}
                  {nameValueTableRows("Source", object_.sources)}
                  </tbody>
                </table>
              </CardContent>
            </Card>
          </Grid>
        ) : null}
        {object_.dates.length > 0 ||
        object_.extents.length > 0 ||
        object_.languages.length > 0 ||
        object_.media.length > 0 ||
        object_.spatials.length > 0 ? (
          <Grid item className="section" id="extent-section">
            <Card>
              <CardHeader title="Extent"/>
              <CardContent>
                <table>
                  <tbody>
                  {nameValueTableRows("Cultural context", object_.culturalContexts)}
                  {nameValueTableRows("Date", object_.dates)}
                  {nameValueTableRows("Extent", object_.extents)}
                  {nameValueTableRows("Language", object_.languages)}
                  {nameValueTableRows("Material", object_.materials)}
                  {nameValueTableRows("Medium", object_.media)}
                  {nameValueTableRows(
                    "Spatial coverage",
                    object_.spatials
                  )}
                  {nameValueTableRows("Technique", object_.techniques)}
                  {nameValueTableRows(
                    "Temporal",
                    object_.temporals
                  )}
                  </tbody>
                </table>
              </CardContent>
            </Card>
          </Grid>
        ) : null}
        {rights ? (
          <Grid item className="section" id="rights-section">
            <Card>
              <CardHeader title="Rights"/>
              <CardContent>
                <RightsTable className="table-bordered" rights={rights}/>
              </CardContent>
            </Card>
          </Grid>
        ) : null}
      </Grid>
    </InstitutionCollectionObjectOverview>
  );
};
