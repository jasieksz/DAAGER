import React, { Component } from 'react';
import '../styles/ManageComponent.css';
import { Button } from 'reactstrap';
import "../../node_modules/react-progress-button/react-progress-button.css";
import ApiService from "../services/ApiService"
import { PullingArgumentsComponent } from "./PullingArgumentsComponent";
import { Alert } from 'reactstrap';
class ManageComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            pullingAddress: this.props.pullingAddress,
            isPulling: false,
            getPullingAddress: true,
            buttonState: '',
            clusterAlias: '',
        };

        this.service = new ApiService();
    }

    handleChangePullingAddress = () => {
        alert('changing pulling address');
        this.setState({
            pullingAddress: '',
            getPullingAddress: true,
            buttonState: ''
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
                                   value={this.state.pullingAddress}
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
                                   placeholder={this.state.clusterAlias}
                            />
                        </div>
                    </div>
                    <div className={'manageButtons'}>
                    </div>
                        </div>
                <PullingArgumentsComponent/>
            </div>
        )
    };

    render() {
        if (this.props.clusterList.length !== 0) {
            return (
                <div>
                    {this.showPullingAddress()}
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

export default ManageComponent;