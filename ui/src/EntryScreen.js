import React, { Component } from 'react';
import { Input, Jumbotron, Container, Row} from 'reactstrap';
import ProgressButton from 'react-progress-button'
import axios from 'axios';
import "../node_modules/react-progress-button/react-progress-button.css";
import "../node_modules/bootstrap/dist/css/bootstrap.css"


class EntryScreen extends Component {
    onFailure;
    onSuccess;

    constructor(props) {
        super(props);
        this.state = {
            isLogged: false,
            buttonState: ''
        };
        this.api = axios.create({
            timeout: 2000,
            withCredentials: true,
            xsrfCookieName:  "DAAGER-TOKEN",
            xsrfHeaderName:  "Csrf-Token",
        });
    }

    componentDidMount() {
        this.api.get("api/hello").then(response => console.log(response));
    }

    verify = () => {
        this.setState({buttonState: 'loading'});
        this.api.post("api/pull/verify", {value: this.input.value}, {timeout: 10000}).then(() => {
            this.setState({isLogged: true});
            this.setState({buttonState: 'success'})
        }).catch(() => {
            if (this.props.onFailure) this.props.onFailure();
            this.setState({buttonState: 'error'})
        });
    };

    start = () => {
        this.api.post("api/pull/start", {value: this.input.value}, {timeout: 10000}).then(() => {
            if (this.props.onSuccess) this.props.onSuccess();
        })
    };

    blockStart = () => this.setState({isLogged: false});

    render() {
        return (
            <div>
                <Jumbotron>
                    <Container>
                        <Row>
                            <h1 className="text-center text-primary">Welcome to DAAGER</h1>
                        </Row>
                        <Row className="mt-3"/>
                        <Row>
                            <Input
                                name="address"
                                id="ageIp"
                                onChange={this.blockStart}
                                placeholder="localhost:1234"
                                innerRef={(input) => this.input = input}
                            />
                        </Row>
                        <Row className="mt-3"/>
                        <Row>
                            {
                                !this.state.isLogged ?
                                <ProgressButton onClick={this.verify} state={this.state.buttonState}>Test</ProgressButton> :
                                <ProgressButton onClick={this.start} state='success'>Start pulling</ProgressButton>
                            }
                        </Row>
                    </Container>
                </Jumbotron>
            </div>
        );
    }

}

export default EntryScreen;