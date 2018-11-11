import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import "../styles/MainPageComponent.css";
import {Button, Modal, ModalBody, ModalFooter, ModalHeader} from 'reactstrap';
import ApiService from "../services/ApiService";
import '../styles/ClustersComponent.css';
import ProgressButton from "react-progress-button";


class ClustersComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            clusterList: [],
            addNewCluster: false,
            newClusterAlias: '',
            newClusterAddress: '',
            newClusterInterval: ''
        };

        this.service = new ApiService();
    }

    cancelAddNewCluster = () => {
        this.setState({
            addNewCluster: false,
            newClusterAlias: '',
            newClusterAddress: '',
            newClusterInterval: ''
        })
    };

    saveAddNewCluster = () => {
        //send request to backend
        this.cancelAddNewCluster();
    };

    addNewCluster = () => {
        this.setState({
            addNewCluster: true
        });
    };

    createAddNewCLusterModal = () => {
        return (
            <Modal isOpen={this.state.addNewCluster}>
                <ModalHeader>Add new Cluster </ModalHeader>
                <ModalBody>
                    <div className={'inputForm'}>
                        <div className="form-group row">
                            <label htmlFor="pullingAddress" className="col-sm-4 col-form-label">Pulling data address</label>
                            <div className="col-sm-8">
                                <input type="text"
                                       className="form-control"
                                       id="pullingAddress"
                                       placeholder="enter pulling address"
                                />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="pullingInterval" className="col-sm-4 col-form-label">Pulling data interval</label>
                            <div className="col-sm-8">
                                <input type="number"
                                       className="form-control"
                                       id="pullingInterval"
                                       placeholder="enter pulling interval"
                                />
                            </div>
                        </div>
                        <div className={'progressButton'}>
                            <ProgressButton
                                onClick={this.verifyPullingAddress}
                                state={this.state.buttonState}
                            >Save
                            </ProgressButton> :
                        </div>
                    </div>
                </ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={this.cancelAddNewCluster}>Close</Button>
                    <Button type="success"
                            className="btn btn-default"
                            onClick={() => this.saveAddNewCluster()}
                    >Save
                    </Button>
                </ModalFooter>
            </Modal>
        );
    };

    createAllClustersTable = () => {
        return (
            <div className={'clustersTable'}>
                Cluster Table
            </div>
        )
    };

    render() {
        return (
            <div className={'clusters'}>
                {this.createAllClustersTable()}
                {this.createAddNewCLusterModal()}
                <Button type="success"
                        className="btn btn-default addNewClusterButton"
                        onClick={() => this.addNewCluster()}
                >Add new cluster
                </Button>
            </div>
        );
    }
}

export default ClustersComponent;