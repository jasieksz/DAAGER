import React, { Component } from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/HomeComponent.css';

class HomeComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
        };
    }

    renderGraph () {
        return (
            <div className={'homeGraph'}>GRAPH</div>
        );
    }

    renderInfo () {
        return (
            <div className={'homeInfo'}>
                <div className={'homeInfoText'}>
                    GRAPH INFO
                </div>
            </div>
        );
    }

    render() {
        return (
            <div>
                <h2 className={'tabTitle'}>HOME COMPONENT </h2>
                <div className={'homeComponents'}>
                    {this.renderInfo()}
                    {this.renderGraph()}
                </div>
            </div>
        );
    }
}

export default HomeComponent;