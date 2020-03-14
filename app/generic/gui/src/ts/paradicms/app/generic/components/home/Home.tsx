import * as React from 'react';
import {Frame} from "paradicms/app/generic/components/frame/Frame";
import {ActiveNavbarItem} from "paradicms/app/generic/components/navbar/ActiveNavbarItem";
import {InstitutionsList} from "paradicms/app/generic/components/institution/InstitutionsList";
import {ApolloQueryWrapper} from "paradicms/app/generic/api/ApolloQueryWrapper";
import {HomeQuery} from "paradicms/app/generic/api/queries/types/HomeQuery";
import * as homeQuery from "paradicms/app/generic/api/queries/homeQuery.graphql";

export const Home: React.FunctionComponent<{}> = () => (
    <ApolloQueryWrapper<HomeQuery> query={homeQuery}>
        {({data}) =>
            <Frame
                activeNavItem={ActiveNavbarItem.Home}
                documentTitle="Home"
                cardTitle="Institutions"
            >
                <InstitutionsList institutions={data.institutions}/>
            </Frame>
        }
    </ApolloQueryWrapper>);
