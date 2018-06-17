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
            nodes: [], //[{id: "n1", label: "Alice"}, {id: "n2", label: "Rabbit"}],
            edges: [], //[{id: "e1", source: "n1", target: "n2"}],
            isModalOpen: false
        };
        this.service = new EntryScreenService();
        this.toggleModal = this.toggleModal.bind(this);
        this.getGraphData();
    }

    toggleModal() {
        console.log(this.state.isModalOpen);
        var prev = this.state.isModalOpen;
        this.setState({
            isModalOpen: !prev
        });
    }

    renderModal() {
        return (
                <Modal isOpen={this.state.isModalOpen}>
                    <ModalHeader>Modal title  </ModalHeader>
                    <ModalBody>
                        Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={this.toggleModal}>Do Something</Button>{' '}
                        <Button color="secondary" onClick={this.toggleModal}>Cancel</Button>
                    </ModalFooter>
                </Modal>
        );
    }

    getGraph() {
        if (this.state.nodes !== []) {
            return (
                <Sigma graph={{
                    nodes: this.state.nodes,
                    edges: this.state.edges
                }}
                       style={{maxWidth: "inherit", height: "inherit"}}
                       onClickNode={e => {
                           this.toggleModal();
                       }}
                       onOverNode={e => console.log("Mouse over node: " + e.data.node.label)}
                       settings={{drawEdges: true, defaultNodeColor: '#0073e6'}}>
                    <RelativeSize initialSize={8}/>
                    <RandomizeNodePositions/>
                </Sigma>
            );
        } else {
            return <div/>
        }
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
            this.service.getGraph().then(data => {
                this.setState({
                    nodes: data.nodes,
                    edges: data.edges
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
                        {this.renderGraph()}
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