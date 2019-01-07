import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/HomeComponent.css';
import Sigma from 'react-sigma/lib/Sigma'
import {RelativeSize} from "react-sigma";
import { Button } from 'reactstrap';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { Alert } from 'reactstrap';
import { Table } from 'reactstrap';
import ApiService from "../services/ApiService";
import Dropdown from 'react-dropdown';
import 'react-dropdown/style.css';
import RandomizeNodePositions from "react-sigma/es/RandomizeNodePositions";

class HomeComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            pullingCluster: this.props.clusterList[0],
            nodes: [],
            edges: [],
            isModalOpen: false,
            modalData: {},
            globalStateData: {},
            clustersButtonExpanded: false,
            nodesDetails: [],
        };

        this.service = new ApiService();

        if (this.state.pullingCluster !== undefined) {
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
        const details = this.state.nodesDetails.filter(i => i.address === node.label);
        this.setState({
            modalData: details[0],
            isModalOpen: true
        });
    };

    createNodeInfoRequest = (address) => {
    return {
            "address": address,
            "clusterAlias": this.state.pullingCluster.alias
        }
    };

    renderModal = () => {
        return (
            <Modal isOpen={this.state.isModalOpen} className={'modal-lg'}>
                <ModalHeader>Info for node {this.state.modalData.address} </ModalHeader>
                    <ModalBody>
                        <Table>
                            <tr>
                                <th scope="row">Address</th>
                                <td> {this.state.modalData.address} </td>
                            </tr>
                            <tr>
                                <th scope="row">Id</th>
                                <td> {this.state.modalData.id} </td>
                            </tr>
                            <tr>
                                <th scope="row">Node type</th>
                                <td> {this.state.modalData.nodeType} </td>
                            </tr>
                            <tr>
                                <th scope="row">Services</th>
                                <td> {(this.state.modalData.services || []).join("\n")} </td>
                            </tr>
                        </Table>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="secondary" onClick={this.toggleModal}>Close</Button>
                    </ModalFooter>
                </Modal>
        );
    };

    getGraph = ()  => {
        const graph = {nodes: this.state.nodes, edges: this.state.edges};
        graph.nodes.forEach( node => {
            const graphNode = this.state.nodesDetails.filter(i => i.address === node.label);
            if (graphNode[0].nodeType === 'UNKNOWN') {
                node.color = '#d14578';
            } else if (graphNode[0].nodeType === 'SATELLITE') {
                node.color = '#0A8A0A';
            } else if (graphNode[0].nodeType === 'COMPUTE') {
                node.color = '#7931b7';
            }
        });

        return (
                <Sigma graph={graph}
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
        this.service.getGlobalState(this.state.pullingCluster.alias).then(response => {
            this.setState({
                globalStateData: response.data[0]
            });
        }).catch(() => {
            console.error('error during getting global state data');
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
                    <tr>
                        <th scope="row">Alias</th>
                        <td> {this.state.globalStateData.clusterAlias} </td>
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
        if (this.state.pullingCluster !== undefined) {
            this.service.getGraph(this.state.pullingCluster.alias).then(response => {
                this.setState({
                    nodes: response.data[0].nodes,
                    edges: response.data[0].edges,
                    nodesDetails: response.data[0].nodesDetails
                });
            }).catch(() => {
                console.error('error during getting graph data');
            });
        }
    };

    handleClusterChanged = (alias) => {
        const newCluster = this.props.clusterList.filter(i => i.alias === alias.value);
        this.setState(
            {pullingCluster: newCluster[0]},
            () => {
                this.getGraphData();
                this.getGlobalData();
                this.render();
            }
        );
    };

    createDropdownMenu = () => {
        const dropdownOptions = [];
        this.props.clusterList.forEach(
            cluster => dropdownOptions.push({
                'value': cluster.alias,
                'label': cluster.alias
            })
        );
        return dropdownOptions
    };


    renderChooseClusterButton = () => {
        return (
            <Dropdown className={'chooseClustersButton'}
                      options={this.createDropdownMenu()}
                      onChange={ (i) => this.handleClusterChanged(i)}
                      value = {this.state.pullingCluster.alias}
                      placeholder="Select an option"/>
        )

    };

    render() {
        if (this.props.clusterList.length !== 0 && this.state.pullingCluster !== undefined) {
            return (
                <div>
                    <div className={'header'}>
                        {this.renderChooseClusterButton()}
                        <h2 className={'tabTitle'}>Home</h2>
                    </div>
                    <div className={'homeComponents'}>
                        {this.renderInfo()}
                        {(this.state.nodes.length) ? this.renderGraph() : <div/>}
                    </div>
                </div>
            );
        } else {
            return (
                <Alert color={"danger"} className={'tabTitle'}>
                    Go to Clusters Tab and set pulling address
                </Alert>
            )
        }
    }
}

export default HomeComponent;