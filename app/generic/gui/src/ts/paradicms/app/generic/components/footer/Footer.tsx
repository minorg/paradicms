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
              <p className="muted">
                This site is for educational purposes only.
              </p>
            </Col>
          </Row>
          <Row>
            <Col className="text-center" xs="12">
              <p>
                <a href={Hrefs.contact}>Contact</a>&nbsp;|&nbsp;<a href={Hrefs.privacy}>Privacy</a>&nbsp;|&nbsp;<a href="https://github.com/minorg/paradicms">GitHub</a>
              </p>
            </Col>
          </Row>
        </Container>
      </footer>
    );
  }
}
