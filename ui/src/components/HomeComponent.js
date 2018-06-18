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

class HomeComponent extends Component {


    constructor(props, context) {
        super(props, context);
        this.state = {
            nodes: [],
            edges: [],
            isModalOpen: false,
            modalData: ''
        };
        this.service = new EntryScreenService();
        this.getGraphData();
    }

    toggleModal = () => {
        var prev = this.state.isModalOpen;
        this.setState({
            isModalOpen: !prev
        });
    };

    getNodeInfo = (node) => {
        // ok -> toggle modal
    };

    renderModal() {
        return (
            <Modal isOpen={this.state.isModalOpen}>
                    <ModalHeader>Info for node {this.state.modalData} </ModalHeader>
                    <ModalBody>
                        Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    </ModalBody>
                    <ModalFooter>
                        <Button color="secondary" onClick={this.toggleModal}>Close</Button>
                    </ModalFooter>
                </Modal>
        );
    }

    getGraph() {
        console.log('getting graph');
        console.log(this.state.nodes);
        console.log(this.state.edges);
        return (
                <Sigma graph={{
                    nodes: [{id: "0", label: "127.0.0.1:12345"}], //this.state.nodes,
                    edges: [{id: "0", source: "0", target: "0"}] //this.state.edges
                }}
                       style={{maxWidth: "inherit", height: "inherit"}}
                       onClickNode={e => {
                           this.toggleModal();
                           this.getNodeInfo(e.data.node)
                       }}
                       onOverNode={e => console.log("Mouse over node: " + e.data.node.label)}
                       settings={{drawEdges: true, defaultNodeColor: '#0073e6'}}>
                    <RelativeSize initialSize={15}/>
                    <RandomizeNodePositions/>
                </Sigma>
            );
    }

    renderGraph() {
        return (
            <div className={'homeGraph'}>
                {this.renderModal()}
                {this.getGraph()}
            </div>
        );
    }

    renderInfo() {
        return (
            <div className={'homeInfo'}>
                <div className={'homeInfoText'}>
                    GRAPH INFO
                </div>
            </div>
        );
    }

    getGraphData = () => {
        if (this.props.pullingAddress !== '') {
            this.service.getGraph().then(response => {
                console.log('response from get response method');
                console.log(response);
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
                    <h2 className={'tabTitle'}>HOME COMPONENT </h2>
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