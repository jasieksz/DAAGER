import React, { Component } from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';
import HomeComponent from "./HomeComponent";
import ManageComponent from "./ManageComponent";
import GrafanaComponent from "./GrafanaComponent";
import "../styles/MainPageComponent.css";
import fontawesome from '@fortawesome/fontawesome'
import faHome from '@fortawesome/fontawesome-free-solid/faHome'
import faCog from '@fortawesome/fontawesome-free-solid/faCog'
import faChartBar from '@fortawesome/fontawesome-free-solid/faChartBar'

class MainPage extends Component {

    constructor(props, context) {
        super(props, context);
        this.handleSelect = this.handleSelect.bind(this);
        this.state = {
            key: 1,
            pullingAddress: '',
        };
        this.handleSetInitPullingData = this.handleSetInitPullingData.bind(this);
    }

    handleSetInitPullingData(pullAddr) {
        this.setState({
            pullingAddress: pullAddr,
        });
    }

    handleSelect(key) {
        this.setState({ key });
    }

    render() {
        return (
            <div>
                <div className={'daagerTitle'}>Daager</div>
                <Tabs>
                    <TabList className={'tabs'}>
                        <Tab eventKey={2} title="manage">
                            <i className={"fas fa-cog fa-fw"}/>
                            {fontawesome.library.add(faCog)}
                            Manage
                        </Tab>
                        <Tab eventKey={1} title="home" >
                            <i className={"fas fa-home fa-fw"}/>
                            {fontawesome.library.add(faHome)}
                            Home
                        </Tab>
                        <Tab eventKey={3} title="grafana">
                            <i className={"fas fa-chart-bar fa-fw"}/>
                            {fontawesome.library.add(faChartBar)}
                            Grafana
                        </Tab>
                    </TabList>

                    <TabPanel>
                        <ManageComponent
                            pullingAddress={this.state.pullingAddress}
                            savePullingInitData={this.handleSetInitPullingData}
                        />
                    </TabPanel>
                    <TabPanel>
                        <HomeComponent
                            pullingAddress={this.state.pullingAddress}/>
                    </TabPanel>
                    <TabPanel>
                        <GrafanaComponent
                            pullingAddress={this.state.pullingAddress}
                        />
                    </TabPanel>
                </Tabs>
            </div>
        );
    }
}

export default MainPage;