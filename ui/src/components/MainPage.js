import React, { Component } from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';
import HomeComponent from "./HomeComponent";
import ManageComponent from "./ManageComponent";
import ClustersComponent from "./ClustersComponent";
import GrafanaComponent from "./GrafanaComponent";
import "../styles/MainPageComponent.css";
import fontawesome from '@fortawesome/fontawesome'
import faHome from '@fortawesome/fontawesome-free-solid/faHome'
import faCog from '@fortawesome/fontawesome-free-solid/faCog'
import faList from '@fortawesome/fontawesome-free-solid/faList'
import faChartBar from '@fortawesome/fontawesome-free-solid/faChartBar'
import ApiService from "../services/ApiService";

class MainPage extends Component {

    constructor(props, context) {
        super(props, context);
        this.handleSelect = this.handleSelect.bind(this);
        this.state = {
            key: 1,
            clusterList : [],
            pullingAddress: '',
        };
        this.service = new ApiService();
        this.getClusterList();
    }

    handleSelect(key) {
        this.setState({ key });
    }

    handleAddNewPullingData = (pullingAddress) => {
        this.setState({
            pullingAddress: pullingAddress,
        });
        this.getClusterList();
    };

    getClusterList = () => {
        this.service.getAllClusters().then( (response) => {
            if (response.data.length !== 0 ) {
                this.setState(
                    {
                        clusterList: response.data,
                        pullingAddress: response.data[0]
                    },
                    () => this.render()
                );
            } else {
                this.setState(
                    {
                        clusterList: [],
                        pullingAddress: ''
                    },
                    () => this.render()
                )
            }
        }).catch((er) => {
            this.setState({buttonState: 'error'});
            console.error('error during getting all clusters' + er);
        });
    };

    render() {
        return (
            <div>
                <div className={'daagerTitle'}>
                    <img src={'logo.png'} alt={''}/>
                </div>
                <Tabs>
                    <TabList className={'tabs'}>
                        <Tab eventKey={1} title="home" >
                            <i className={"fas fa-home fa-fw"}/>
                            {fontawesome.library.add(faHome)}
                            Home
                        </Tab>
                        <Tab eventKey={2} title="grafana">
                            <i className={"fas fa-chart-bar fa-fw"}/>
                            {fontawesome.library.add(faChartBar)}
                            Grafana
                        </Tab>
                        <Tab eventKey={3} title="manage">
                            <i className={"fas fa-cog fa-fw"}/>
                            {fontawesome.library.add(faCog)}
                            Manage
                        </Tab>
                        <Tab eventKey={4} title="clusters">
                            <i className={"fas fa-list fa-fw"}/>
                            {fontawesome.library.add(faList)}
                            Clusters
                        </Tab>
                    </TabList>

                    <TabPanel>
                        <HomeComponent
                            clusterList={this.state.clusterList}
                        />
                    </TabPanel>
                    <TabPanel>
                        <GrafanaComponent
                            pullingAddress={this.state.pullingAddress}
                        />
                    </TabPanel>
                    <TabPanel>
                        <ManageComponent
                            clusterList={this.state.clusterList}
                        />
                    </TabPanel>
                    <TabPanel>
                        <ClustersComponent
                            savePullingInitData={this.handleAddNewPullingData}
                            getClusterList={this.getClusterList}
                            clusterList={this.state.clusterList}
                        />
                    </TabPanel>
                </Tabs>
            </div>
        );
    }
}

export default MainPage;