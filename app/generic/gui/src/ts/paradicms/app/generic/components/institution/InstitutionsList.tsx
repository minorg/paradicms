import * as React from "react";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { List, ListItem, ListItemText } from "@material-ui/core";

interface Institution {
  name: string;
  uri: string;
}

const ListItemLink = (props: any) => {
  return <ListItem button component="a" {...props} />;
}

export const InstitutionsList: React.FunctionComponent<{
  institutions: Institution[];
}> = ({institutions}) => (
  <List>
    {institutions.map(institution => (
      <ListItemLink key={institution.uri} href={Hrefs.institution(institution.uri)}>
        <ListItemText>{institution.name}</ListItemText>
      </ListItemLink>
    ))}
  </List>
);
