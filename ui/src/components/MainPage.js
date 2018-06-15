import React, { Component } from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';
import HomeComponent from "./HomeComponent";
import ManageComponent from "./ManageComponent";
import GrafanaComponent from "./GrafanaComponent";
import "../styles/MainPageComponent.css";

class MainPage extends Component {

    constructor(props, context) {
        super(props, context);
        this.handleSelect = this.handleSelect.bind(this);
        this.state = {
            showLogin: true,
            key: 2
        };
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
                        <Tab eventKey={1} title="home">
                            Home
                        </Tab>
                        <Tab eventKey={2} title="manage">
                           Manage
                        </Tab>
                        <Tab eventKey={3} title="grafana">
                            Grafana
                        </Tab>
                    </TabList>

                    <TabPanel>
                        <HomeComponent/>
                    </TabPanel>
                    <TabPanel>
                        <ManageComponent/>
                    </TabPanel>
                    <TabPanel>
                        <GrafanaComponent/>
                    </TabPanel>
                </Tabs>
            </div>
        );
    }
}

export default MainPage;