import { Hrefs } from "paradicms/app/generic/Hrefs";
import * as React from "react";
import { Col, Container, Row } from "reactstrap";

export class Footer extends React.Component {
  render() {
    return (
      <footer>
        <Container fluid>
          <Row>
            <Col className="text-center" xs="12">
              <a href={Hrefs.contact}>Contact</a>&nbsp;|&nbsp;
            </Col>
          </Row>
          <Row>&nbsp;</Row>
          <Row>
            <Col className="text-center" xs="12">
              <p className="muted">
                This site is for educational purposes only.
              </p>
              <p>
                Powered by <a href="https://github.com/minorg/paradicms">paradicms</a>
              </p>
            </Col>
          </Row>
        </Container>
      </footer>
    );
  }
}
