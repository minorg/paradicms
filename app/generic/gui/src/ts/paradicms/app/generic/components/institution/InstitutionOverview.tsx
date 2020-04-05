import { RouteComponentProps } from "react-router";
import * as React from "react";
import { CollectionsList } from "paradicms/app/generic/components/collection/CollectionsList";
import * as InstitutionOverviewQueryDocument from "paradicms/app/generic/api/queries/InstitutionOverviewQuery.graphql";
import {
  InstitutionOverviewQuery,
  InstitutionOverviewQueryVariables
} from "paradicms/app/generic/api/queries/types/InstitutionOverviewQuery";
import { InstitutionCollectionObjectOverview } from "paradicms/app/generic/components/frame/InstitutionCollectionObjectOverview";
import { useQuery } from "@apollo/react-hooks";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import * as ReactLoader from "react-loader";
import { ApolloException } from "@paradicms/base";

export const InstitutionOverview: React.FunctionComponent<RouteComponentProps<{
  institutionUri: string;
}>> = ({match}) => {
  const institutionUri = decodeURIComponent(match.params.institutionUri);

  const { data, error, loading } = useQuery<InstitutionOverviewQuery, InstitutionOverviewQueryVariables>(InstitutionOverviewQueryDocument, {
    variables: {
      institutionUri
    }
  });

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false}/>;
  } else if (!data) {
    throw new EvalError();
  }

  return (
    <InstitutionCollectionObjectOverview
      institutionName={data.institutionByUri.name}
      institutionUri={institutionUri}
      title={data.institutionByUri.name + " - Collections"}
    >
      <CollectionsList
        collections={data.institutionByUri.collections.map(collection => ({
          institutionUri,
          ...collection,
        }))}
      />
    </InstitutionCollectionObjectOverview>
  );
};
