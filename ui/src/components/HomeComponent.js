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
import RandomizeNodePositions from "react-sigma/es/RandomizeNodePositions";

class HomeComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            nodes: [],
            edges: [],
            isModalOpen: false,
            modalData: {},
            globalStateData: {},
            clustersButtonExpanded: true,
        };

        this.service = new ApiService();
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
        const graph = {nodes: this.state.nodes, edges: this.state.edges};
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
                this.setState({
                    nodes: response.data[0].nodes,
                    edges: response.data[0].edges
                });
            }).catch(() => {
                console.log('error during getting graph data');
            });
        }
    };

    getClusterList = () => {
        //request
    };

    handleClustersButtonChange= () => {
        this.setState({
            clustersButtonExpanded: true
        });
    };

    renderChoooseClusterButton = () => {
        return (
        <div className="dropdown show chooseClustersButton">
            <a className="btn btn-secondary dropdown-toggle" href="#" role="button" id="dropdownMenuLink"
               data-toggle="dropdown" aria-haspopup="true" aria-expanded={this.state.clustersButtonExpanded}
            onClick={this.handleClustersButtonChange}>
                Dropdown link
            </a>

            <div className="dropdown-menu" aria-labelledby="dropdownMenuLink">
                <a className="dropdown-item" href="#">Action</a>
                <a className="dropdown-item" href="#">Another action</a>
                <a className="dropdown-item" href="#">Something else here</a>
            </div>
        </div>
        );

    };

    render() {
        if (this.props.pullingAddress !== '') {
            return (
                <div>
                    <div className={'header'}>
                        {this.renderChoooseClusterButton()}
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
                    Go to Manage Tab and set pulling address
                </Alert>
            )
        }
    }
}

export default HomeComponent;