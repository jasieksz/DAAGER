import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/GrafanaComponent.css';
import {Alert} from 'reactstrap';

class GrafanaComponent extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
                <iframe height={'100%'} width={'100%'} src="http://localhost:3001"/>
        );
    }
}

export default GrafanaComponent;