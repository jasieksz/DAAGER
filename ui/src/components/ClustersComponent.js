import React, {Component} from 'react';
import 'react-tabs/style/react-tabs.css';
import "../styles/MainPageComponent.css";
import {Button, Modal, ModalBody, ModalFooter, ModalHeader, Table} from 'reactstrap';
import ApiService from "../services/ApiService";
import '../styles/ClustersComponent.css';
import ProgressButton from "react-progress-button";
import fontawesome from "@fortawesome/fontawesome";
import {faTrashAlt} from "@fortawesome/fontawesome-free-solid";
import '../styles/ClustersComponent.css';


class ClustersComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            addNewCluster: false,
            buttonState: '',
        };

        this.service = new ApiService();
    }

    deletePullingAddress = (cluster) => {
        this.service.stopPullingParam({
            "clusterAlias": cluster.alias,
            "workerAddress": cluster.baseAddress,
        }).then(() => {
        this.props.getClusterList();
        }).catch((er) => {
            console.error('Error during deleting address' + er);
        });
    };

    cancelAddNewCluster = () => {
        this.setState({
            addNewCluster: false,
        })
    };

    componentDidMount() {
        this.service.hello().then();
    }

    addNewCluster = () => {
        this.setState({
            addNewCluster: true
        });
    };

    verifyPullingAddress = () => {
        const pullingIntervalValue = document.getElementById("pullingInterval").value;
        const pullingAddressValue = document.getElementById("pullingAddress").value;
        const clusterAliasValue = document.getElementById("clusterAlias").value;
        // verify cluster alias if exists
        if (pullingIntervalValue === ''){
            alert('pulling interval cannot be empty');
            return;
        }
        if (clusterAliasValue === ''){
            alert('cluster alias cannot be empty');
            return;
        }
        this.setState({
            buttonState: 'loading'
        });
        this.service.verify({value: pullingAddressValue}).then(clusterId => {
            this.setState({
                buttonState: 'success',
                addNewCluster: false
            });
            this.saveInitPullingData(pullingAddressValue, pullingIntervalValue, clusterId.data, clusterAliasValue)
        }).catch((er) => {
            console.error(er);
            alert("Something is wrong with the address");
            this.setState({buttonState: 'error'})
        });
    };

    saveInitPullingData(pullingAddress, pullingInterval, clusterId, clusterAlias) {
        this.service.start({
            "baseAddress": pullingAddress,
            "interval": parseInt(pullingInterval),
            "clusterId": clusterId,
            alias: clusterAlias
        }).then(() => {
            this.props.savePullingInitData(pullingAddress);
        }).catch((er) => {
            this.setState({buttonState: 'error'});
            console.error('error during starting pulling data ' + er);
        });
    };

    createAddNewClusterModal = () => {
        return (
            <Modal isOpen={this.state.addNewCluster}>
                <ModalHeader className={'modalHeader'}>Add new Cluster </ModalHeader>
                <ModalBody>
                    <div className={'inputForm'}>
                        <div className="form-group row">
                            <label htmlFor="pullingAddress" className="col-sm-4 col-form-label">Pulling data
                                address: </label>
                            <div className="col-sm-8">
                                <input type="text"
                                       className="form-control"
                                       id="pullingAddress"
                                       placeholder="enter pulling address"
                                />
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="pullingInterval" className="col-sm-4 col-form-label">Pulling data
                                interval: </label>
                            <div className="col-sm-8">
                                <input type="number"
                                       className="form-control"
                                       id="pullingInterval"
                                       placeholder="enter pulling interval"
                                />
                            </div>
                        </div>
                        <div className="row form-group">
                            <label htmlFor="clusterAlias" className="col-sm-4 col-form-label">Cluster alias: </label>
                            <div className="col-sm-8">
                                <input type="text"
                                       className="form-control"
                                       id={'clusterAlias'}
                                       placeholder="enter cluster alias"
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
                    <Button className={'modalCloseButton'} color="secondary"
                            onClick={this.cancelAddNewCluster}>Close</Button>
                </ModalFooter>
            </Modal>
        );
    };

    createTableData = () => {
        return this.props.clusterList.map(i =>
            <tr>
                <th scope="row">{i.baseAddress}</th>
                <td> {i.alias} </td>
                <td> {i.clusterId} </td>
                <td>
                    <Button className="btn"
                            onClick={() => this.deletePullingAddress(i)}
                    > <i className={"fas fa-trash-alt fa-fw"}/>
                        {fontawesome.library.add(faTrashAlt)}
                    </Button>
                </td>
            </tr>);
    };

    createAllClustersTable = () => {
        return (
            <div className={'clustersTable'}>
                <Table>
                    <thead>
                    <tr>
                        <th>Pulling Address</th>
                        <th>Cluster Alias</th>
                        <th> Cluster Id </th>
                        <th>Delete</th>
                    </tr>
                    </thead>
                    <tbody> {this.createTableData()} </tbody>
                </Table>
            </div>
        );
    };

    render() {
        return (
            <div className={'clusters'}>
                {this.createAddNewClusterModal()}
                {this.createAllClustersTable()}
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