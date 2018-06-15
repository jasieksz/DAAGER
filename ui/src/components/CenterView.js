import React, { Component } from 'react';
import { Row , Container, Col} from 'reactstrap';
import '../styles/CenterView.css';

class CenterView extends Component {
    render() {
        return (
            <Container fluid className="centerContainer" >
                <Row>
                    <Col/>
                    <Col>{this.props.children}</Col>
                    <Col/>
                </Row>
                <Row/>
            </Container>
        );
    }
}

export default CenterView;