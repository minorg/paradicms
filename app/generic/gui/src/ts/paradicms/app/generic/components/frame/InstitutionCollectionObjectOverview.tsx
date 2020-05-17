import { Link } from "react-router-dom";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import * as React from "react";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import { stripHtml } from "@paradicms/base";
import { Typography } from "@material-ui/core";

export const InstitutionCollectionObjectOverview: React.FunctionComponent<{
  children: React.ReactNode;
  description?: string;
  institutionName: string;
  institutionUri: string;
  collectionName?: string;
  collectionUri?: string;
  objectTitle?: string;
  objectUri?: string;
  title: string;
}> = props => {
  let breadcrumbItems: React.ReactNode[] = [
    <Link to={Hrefs.home}>Home</Link>,
    <Typography>Institutions</Typography>,
    <Link to={Hrefs.institution(props.institutionUri)}>
      {props.institutionName}
    </Link>
  ];
  if (props.collectionName && props.collectionUri) {
    breadcrumbItems = breadcrumbItems.concat(
      <Typography>Collections</Typography>,
      <Link
        to={Hrefs.collection({
          collectionUri: props.collectionUri,
          institutionUri: props.institutionUri,
        })}
      >
        {props.collectionName}
      </Link>
    );

    if (props.objectTitle && props.objectUri) {
      breadcrumbItems = breadcrumbItems.concat(
        <Typography>Objects</Typography>,
        <Link
          to={Hrefs.object({
            collectionUri: props.collectionUri,
            institutionUri: props.institutionUri,
            objectUri: props.objectUri,
          })}
        >
          {props.objectTitle}
        </Link>
      );
    }
  }

  return (
    <Frame
      breadcrumbItems={breadcrumbItems}
      documentTitle={[
        props.institutionName,
        props.collectionName,
        props.objectTitle,
      ]
        .filter(name => !!name)
        .join(" - ")}
      cardTitle={props.title}
    >
      {props.description ? <p>{stripHtml(props.description)}</p> : null}
      {props.children}
    </Frame>
  );
}
