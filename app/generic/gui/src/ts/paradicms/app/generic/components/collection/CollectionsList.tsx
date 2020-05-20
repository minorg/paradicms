import * as React from "react";
import { Hrefs } from "paradicms/app/generic/Hrefs";
import { List, ListItem, ListItemText } from "@material-ui/core";

interface Collection {
  institutionUri: string;
  name: string;
  uri: string;
}

const ListItemLink = (props: any) => {
  return <ListItem button component="a" {...props} />;
}

export const CollectionsList: React.FunctionComponent<{
  collections: Collection[];
}> = ({collections}) => (
  <List>
    {collections.map(collection => (
      <ListItemLink key={collection.uri} href={Hrefs.collection({
            collectionUri: collection.uri,
            institutionUri: collection.institutionUri,
          })}
        >
          <ListItemText>{collection.name}</ListItemText>
      </ListItemLink>
    ))}
  </List>
);
