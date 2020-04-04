import * as React from "react";
import { Frame } from "paradicms/app/generic/components/frame/Frame";
import { ActiveNavbarItem } from "paradicms/app/generic/components/navbar/ActiveNavbarItem";
import { InstitutionsList } from "paradicms/app/generic/components/institution/InstitutionsList";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import * as ReactLoader from "react-loader";
import { HomeQuery } from "paradicms/app/generic/api/queries/types/HomeQuery";
import { useQuery } from "@apollo/react-hooks";
import { ApolloException } from "paradicms-base";
import * as HomeQueryDocument from "paradicms/app/generic/api/queries/HomeQuery.graphql";

export const Home: React.FunctionComponent = () => {
  const { data, error, loading } = useQuery<HomeQuery>(HomeQueryDocument);

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false}/>;
  } else if (!data) {
    throw new EvalError();
  }

  return (
    <Frame
      activeNavItem={ActiveNavbarItem.Home}
      documentTitle="Home"
      cardTitle="Institutions"
    >
      <InstitutionsList institutions={data.institutions}/>
    </Frame>
  );
}
