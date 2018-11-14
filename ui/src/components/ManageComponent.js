import React, { Component } from 'react';
import '../styles/ManageComponent.css';
import { Button } from 'reactstrap';
import "../../node_modules/react-progress-button/react-progress-button.css";
import ApiService from "../services/ApiService"
import { PullingArgumentsComponent } from "./PullingArgumentsComponent";
import { Alert } from 'reactstrap';
import Dropdown from "react-dropdown";
class ManageComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            pullingCluster: this.props.clusterList[0],
            isPulling: false,
        };

        this.service = new ApiService();
    }

    handleClusterChanged = (alias) => {
        const newCluster = this.props.clusterList.filter(cluster => cluster.alias === alias.value);
        this.setState(
            {pullingCluster: newCluster[0]},
            () => this.render());
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

    handleChangePullingAddress = () => {
        alert('changing pulling address');
        this.setState({
            pullingCluster: '',
        });
    };

    showPullingAddress =() => {
        return (
            <div>
                <div className={'pullingDataAddressBox'}>
                    <div className="row pullingAddr">
                        <label htmlFor="pullingAddress" className="col-sm-4 col-form-label">Pulling data address</label>
                        <div className="col-sm-6">
                            <input type="text"
                                   className="form-control"
                                   value={this.state.pullingCluster.baseAddress}
                            />
                        </div>
                    </div>
                    <div className={'manageButtons'}>
                        <Button color="warning"
                                className="btn btn-default manageButton"
                                onClick={this.handleChangePullingAddress}
                        >Change Pulling Address
                        </Button>
                    </div>
                </div>
                <div className={'clusterAliasBox'}>
                    <div className="row clusterAlias">
                        <label htmlFor="pullingAddress" className="col-sm-4 col-form-label">Cluster alias: </label>
                        <div className="col-sm-6">
                            <input type="text"
                                   className="form-control"
                                   id={'clusterAliasValue'}
                                   placeholder={this.state.pullingCluster.alias}
                            />
                        </div>
                    </div>
                </div>
                <PullingArgumentsComponent
                    pullingCluster={this.state.pullingCluster}
                />
            </div>
        )
    };

    render() {
        if (this.props.clusterList.length !== 0) {
            return (
                <div>
                    <div className={'header'}>
                        {this.renderChooseClusterButton()}
                        <h2 className={'tabTitle'}>Manage Cluster</h2>
                    </div>
                    {this.showPullingAddress()}
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

export default ManageComponent;