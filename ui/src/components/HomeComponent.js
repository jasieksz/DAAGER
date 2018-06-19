import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/HomeComponent.css';
import Sigma from 'react-sigma/lib/Sigma'
import {RelativeSize} from "react-sigma";
import RandomizeNodePositions from "react-sigma/es/RandomizeNodePositions";
import { Button } from 'reactstrap';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { Alert } from 'reactstrap';
import EntryScreenService from "../services/EntryScreenService";
import { Table } from 'reactstrap';
import _ from "lodash";

class HomeComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            nodes: [],
            edges: [],
            isModalOpen: false,
            modalData: {},
            globalStateData: {},
        };

        this.service = new EntryScreenService();
        if (this.props.pullingAddress !== '') {
            this.getGraphData();
            this.getGlobalData();
        }
    }

    toggleModal = () => {
        const prev = this.state.isModalOpen;
        this.setState({
            isModalOpen: !prev
        });
    };

    getNodeDetailInfo = (node) => {
        this.service.getNodeDetailInfo(this.createNodeInfoRequest(node.label)).then(response => {
            this.setState({
                modalData: response.data
            });
            this.toggleModal();
        }).catch((err) => {
            console.log('error during getting node detail data');
            console.log(err);
        });
    };

    createNodeInfoRequest(address) {
        return {
            "address": address
        }
    }

    renderModal() {
        return (
            <Modal isOpen={this.state.isModalOpen}>
                <ModalHeader>Info for node {this.state.modalData.address} </ModalHeader>
                    <ModalBody>
                        <Table>
                            <tr>
                                <th scope="row">Address</th>
                                <td> {this.state.modalData.address} </td>
                            </tr>
                            <tr>
                                <th scope="row">Last Message</th>
                                <td> {this.state.modalData.lastMsg} </td>
                            </tr>
                            <tr>
                                <th scope="row">Cpu</th>
                                <td> {this.state.modalData.cpu} </td>
                            </tr>
                            <tr>
                                <th scope="row">Memory</th>
                                <td> {this.state.modalData.memory} </td>
                            </tr>
                        </Table>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="secondary" onClick={this.toggleModal}>Close</Button>
                    </ModalFooter>
                </Modal>
        );
    }

    getGraph = ()  => {
        console.log('getting graph');
        console.log(this.state.nodes);
        console.log(this.state.edges);
        // const nodes = _.map(this.state.nodes, i => return {{"id": i.id, "label": i.label}});
        return (
                <Sigma graph={{
                    nodes: [{"id": "0", "label": "127.0.0.1:12345"}],
                    edges: [{id: "0", source: "0", target: "0"}] //this.state.edges
                }}
                       style={{maxWidth: "inherit", height: "inherit"}}
                       onClickNode={e => {
                           this.getNodeDetailInfo(e.data.node)
                       }}
                       onOverNode={e => console.log("Mouse over node: " + e.data.node.label)}
                       settings={{drawEdges: true, defaultNodeColor: '#0073e6'}}>
                    <RelativeSize initialSize={20}/>
                    <RandomizeNodePositions/>
                </Sigma>
            );
    };

    renderGraph() {
        return (
            <div className={'homeGraph'}>
                {this.renderModal()}
                {this.getGraph()}
            </div>
        );
    }

    getGlobalData = () => {
        this.service.getGloalState().then( response => {
            this.setState({
                globalStateData: response.data
            });
        }).catch(() => {
            console.log('error during getting global state data');
        });
    };

    renderGobalStateData() {
        if (this.state.globalStateData !== {}) {
            return (
                <Table>
                    <tr>
                        <th scope="row">Number of nodes</th>
                        <td> {this.state.globalStateData.nodesCount} </td>
                    </tr>
                    <tr>
                        <th scope="row">Base Address</th>
                        <td> {this.state.globalStateData.baseAddress} </td>
                    </tr>
                    <tr>
                        <th scope="row">Status</th>
                        <td> {this.state.globalStateData.status} </td>
                    </tr>
                </Table>
            );
        } else {
            this.getGlobalData();
            return <div/>;
        }
    }

    renderInfo() {
        return (
            <div className={'homeInfo'}>
                <div className={'homeInfoText'}>
                    <h5> Graph Info </h5>
                    {this.renderGobalStateData()}
                </div>
            </div>
        );
    }

    getGraphData = () => {
        if (this.props.pullingAddress !== '') {
            this.service.getGraph().then(response => {
                console.log(response.data[0]);
                this.setState({
                    nodes: response.data[0].nodes,
                    edges: response.data[0].edges
                });
            }).catch(() => {
                console.log('error during getting graph data');
            });
        }
    };

    render() {
        if (this.props.pullingAddress !== '') {
            return (
                <div>
                    <h2 className={'tabTitle'}>Home</h2>
                    <div className={'homeComponents'}>
                        {this.renderInfo()}
                        {(this.state.nodes !== []) ? this.renderGraph() : <div/>}
                    </div>
                </div>
            );
        } else {
            return (
                <Alert color={"danger"} className={'tabTitle'}>
                    Go to Manage Tab and set pulling address
                </Alert>
            )
        }
    }
}

export default HomeComponent;