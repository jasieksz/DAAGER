import React, { Component } from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/GrafanaComponent.css';
import { Alert } from 'reactstrap';

class GrafanaComponent extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        if (this.props.pullingAddress !== '') {
            return (
                <div>
                    <h2 className={'tabTitle'}>Grafana Component </h2>
                    <div className={'grafanaLink'}>
                        link do grafany
                    </div>
                </div>
            );
        } else {
            return (
                <Alert color={"danger"} className={'tabTitle'}>
                    Go to Manage Tab and set pulling address
                </Alert>
            );
        }
    }
}

export default GrafanaComponent;