import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import '../styles/HomeComponent.css';
import Sigma from 'react-sigma/lib/Sigma'
import {RelativeSize} from "react-sigma";
import RandomizeNodePositions from "react-sigma/es/RandomizeNodePositions";
import { Button, Popover, PopoverHeader, PopoverBody } from 'reactstrap';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

class HomeComponent extends Component {


    constructor(props, context) {
        super(props, context);
        this.state = {
            nodes: [{id: "n1", label: "Alice"}, {id: "n2", label: "Rabbit"}],
            edges: [{id: "e1", source: "n1", target: "n2"}],
            isModalOpen: false
            // popOversOpen: new Map()
        };
        this.toggleModal = this.toggleModal.bind(this);
        // this.setPopOvers();
    }

    // setPopOvers() {
    //     this.state.nodes.forEach( node => this.state.popOversOpen.set({"id": node.id,"isOpen": true}));
    //     console.log(this.state.popOversOpen);
    // }
    //
    // togglePopover(id) {
    //     console.log("id: " + id);
    //     var prevState = this.state.popOversOpen.get(id);
    //     console.log(prevState);
    //     this.state.popOversOpen.set(id, !prevState);
    // }
    //
    // renderPopOvers() {
    //     return (
    //         this.state.nodes.map( i =>
    //             <Popover placement="auto" isOpen={this.state.popOversOpen.get(i.id)} toggle={this.togglePopover(i.id)}>
    //                 <PopoverHeader>Popover Title</PopoverHeader>
    //                 <PopoverBody>Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem
    //                     lacinia quam venenatis vestibulum.</PopoverBody>
    //             </Popover>
    //         )
    //     );
    // }

    toggleModal() {
        console.log(this.state.isModalOpen);
        var prev = this.state.isModalOpen;
        this.setState({
            isModalOpen: !prev
        });
        console.log(this.state.isModalOpen);
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
        return (
                <Sigma graph={{
                    nodes: this.state.nodes,
                    edges: this.state.edges
                    }}
                   style={{maxWidth: "inherit", height: "inherit"}}
                   onClickNode={e => { this.toggleModal();} }
                   onOverNode={e => console.log("Mouse over node: " + e.data.node.label)}
                   settings={{drawEdges: true, defaultNodeColor: '#0073e6'}}>
                <RelativeSize initialSize={8}/>
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