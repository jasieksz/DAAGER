import React, { Component } from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';

class MainPage extends Component {

    constructor(props, context) {
        super(props, context);
        this.handleSelect = this.handleSelect.bind(this);
        this.state = {
            showLogin: true,
            key: 1
        };
    }

    handleSelect(key) {
        this.setState({ key });
    }

    render() {
        return (
            <Tabs>
                <TabList>
                    <Tab eventKey={1} title="Tab 1">
                        Tab 1 content
                    </Tab>
                    <Tab eventKey={2} title="Tab 2">
                        Tab 2 content
                    </Tab>
                    <Tab eventKey={3} title="Tab 3" disabled>
                        Tab 3 content
                    </Tab>
                </TabList>

                <TabPanel>
                    <h2>Any content 1</h2>
                </TabPanel>
                <TabPanel>
                    <h2>Any content 2</h2>
                </TabPanel>
                <TabPanel>
                    <h2>Any content 3</h2>
                </TabPanel>
            </Tabs>
        );
    }
}

export default MainPage;