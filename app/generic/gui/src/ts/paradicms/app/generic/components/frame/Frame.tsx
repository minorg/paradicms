import "./Frame.scss";

import * as classnames from "classnames";
import { Footer } from "paradicms/app/generic/components/footer/Footer";
import { ActiveNavbarItem } from "paradicms/app/generic/components/navbar/ActiveNavbarItem";
import Navbar from "paradicms/app/generic/components/navbar/Navbar";
import * as React from "react";
import { useEffect } from "react";
import { Breadcrumb, Card, CardBody, CardHeader, CardTitle, Col, Container, Row } from "reactstrap";
import { ApolloQueryWrapper } from "paradicms/app/generic/api/ApolloQueryWrapper";
import { FrameQuery } from "paradicms/app/generic/api/queries/types/FrameQuery";
import * as FrameQueryDocument from "paradicms/app/generic/api/queries/FrameQuery.graphql";

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
  return (
    <ApolloQueryWrapper<FrameQuery> query={FrameQueryDocument}>
      {({data}) => (
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
        </div>
      )}
    </ApolloQueryWrapper>
  );
};
