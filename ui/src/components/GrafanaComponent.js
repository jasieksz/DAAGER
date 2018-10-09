import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/GrafanaComponent.css';

class GrafanaComponent extends Component {

    render() {
        return (
                <iframe title="grafana" height={'100%'} width={'100%'} src="http://localhost:3001"/>
        );
    }
}

export default GrafanaComponent;