import React, { Component } from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/GrafanaComponent.css';

class GrafanaComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
        };
    }

    render() {
        return (
            <div>
                <h2 className={'tabTitle'}>Grafana Component </h2>
                <div className={'grafanaLink'}>
                   link do grafany
                </div>
            </div>
        );
    }
}

export default GrafanaComponent;