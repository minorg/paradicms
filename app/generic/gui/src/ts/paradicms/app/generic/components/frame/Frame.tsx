import "./Frame.scss";

import * as classnames from "classnames";
import { Footer } from "paradicms/app/generic/components/footer/Footer";
import { ActiveNavbarItem } from "paradicms/app/generic/components/navbar/ActiveNavbarItem";
import Navbar from "paradicms/app/generic/components/navbar/Navbar";
import * as React from "react";
import { useEffect } from "react";
import { Breadcrumb, Card, CardBody, CardHeader, CardTitle, Col, Container, Row } from "reactstrap";
import { FrameQuery } from "paradicms/app/generic/api/queries/types/FrameQuery";
import * as FrameQueryDocument from "paradicms/app/generic/api/queries/FrameQuery.graphql";
import { GenericErrorHandler } from "paradicms/app/generic/components/error/GenericErrorHandler";
import * as ReactLoader from "react-loader";
import { useQuery } from "@apollo/react-hooks";
import { ApolloException } from "@paradicms/base";

export const Frame: React.FunctionComponent<{
  activeNavItem?: ActiveNavbarItem;
  breadcrumbItems?: React.ReactNode;
  cardTitle?: React.ReactNode;
  children: React.ReactNode;
  className?: string;
  documentTitle: string;
}> = ({
  activeNavItem,
  breadcrumbItems,
  cardTitle,
  className,
  children,
  documentTitle,
}) => {
  useEffect(() => {
    document.title = "Paradicms - " + documentTitle;
  });

  const { data, error, loading } = useQuery<FrameQuery>(FrameQueryDocument);

  if (error) {
    return <GenericErrorHandler exception={new ApolloException(error)}/>;
  } else if (loading) {
    return <ReactLoader loaded={false}/>;
  } else if (!data) {
    throw new EvalError();
  }

  return (
    <div className={classnames(["frame", className])}>
      <Navbar
        activeNavItem={activeNavItem}
        currentUser={data.currentUser ? data.currentUser : undefined}
      />
      <div className="mb-2 mt-2">
        <Container fluid>
          {breadcrumbItems ? (
            <Row>
              <Col className="px-0" xs="12">
                <Breadcrumb>{breadcrumbItems}</Breadcrumb>
              </Col>
            </Row>
          ) : null}
          <Row>
            <Col className="px-0" xs="12">
              <Card>
                <CardHeader>
                  <CardTitle>
                    <h2>{cardTitle ? cardTitle : documentTitle}</h2>
                  </CardTitle>
                </CardHeader>
                <CardBody>{children}</CardBody>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
      <Footer />
      {/*{Environment.development ? <DevTools /> : null}*/}
    </div>);
};
