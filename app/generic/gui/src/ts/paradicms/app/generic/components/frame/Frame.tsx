import { Footer } from "paradicms/app/generic/components/footer/Footer";
import { Navbar } from "paradicms/app/generic/components/navbar/Navbar";
import * as React from "react";
import { useEffect } from "react";
import { FrameQuery } from "paradicms/app/generic/api/queries/types/FrameQuery";
import * as FrameQueryDocument from "paradicms/app/generic/api/queries/FrameQuery.graphql";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import * as ReactLoader from "react-loader";
import { useQuery } from "@apollo/react-hooks";
import { ApolloException } from "@paradicms/base";
import { Breadcrumbs, Card, CardContent, CardHeader, Grid, makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  breadcrumbs: {
    marginLeft: theme.spacing(4)
  },
  card: {
    marginLeft: theme.spacing(4),
    marginRight: theme.spacing(4),
  }
}));

export const Frame: React.FunctionComponent<{
  breadcrumbItems?: React.ReactNode[];
  cardTitle?: React.ReactNode;
  children: React.ReactNode;
  className?: string;
  documentTitle: string;
  onSearch?: (text: string) => void;
}> = ({
  breadcrumbItems,
  cardTitle,
  children,
  documentTitle,
  onSearch
}) => {
  useEffect(() => {
    document.title = "Paradicms - " + documentTitle;
  });

  const { data, error, loading } = useQuery<FrameQuery>(FrameQueryDocument);

  const classes = useStyles();

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false}/>;
  } else if (!data) {
    throw new EvalError();
  }

  return (
    <Grid data-cy="frame" container direction="column" spacing={2}>
      <Grid item>
        <Navbar
          currentUser={data.currentUser ? data.currentUser : undefined}
          onSearch={onSearch}
        />
      </Grid>
      {breadcrumbItems ? (
        <Grid item data-cy="frame-breadcrumbs-row">
          <Breadcrumbs className={classes.breadcrumbs} data-cy="frame-breadcrumbs">
            {breadcrumbItems}
          </Breadcrumbs>
        </Grid>
      ) : null}
      <Grid item>
        <Card className={classes.card}>
          <CardHeader data-cy="frame-card-header" title={cardTitle ? cardTitle : documentTitle}/>
          <CardContent>
            {children}
          </CardContent>
        </Card>
      </Grid>
      <Grid item>
        <Footer />
      </Grid>
    </Grid>);
};
