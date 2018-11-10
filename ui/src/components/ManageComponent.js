import React, { Component } from 'react';
import '../styles/ManageComponent.css';
import { Button } from 'reactstrap';
import ProgressButton from 'react-progress-button'
import "../../node_modules/react-progress-button/react-progress-button.css";
import ApiService from "../services/ApiService"
import { PullingArgumentsComponent } from "./PullingArgumentsComponent";
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
        this.checkIfAlreadyPulling();
        this.saveInitPullingData = this.saveInitPullingData.bind(this);

        this.handleChangePullingAddress = this.handleChangePullingAddress.bind(this);
        this.showPullingAddress = this.showPullingAddress.bind(this);
        this.saveClusterAlias = this.saveClusterAlias.bind(this);
    }

    checkIfAlreadyPulling = ()  => {
        this.service.getGloalState().then( (response) => {
            if (response.data.status === 'OK') {
                this.setState({
                    pullingAddress: response.data.baseAddress,
                    getPullingAddress: false
                });
                this.props.savePullingInitData(response.data.baseAddress);
            }
        }).catch((er) => {
            console.log('not set ' + er);
        });
    };

    componentDidMount() {
        this.service.hello().then(response => console.log(response));
    }

    verifyPullingAddress = () => {
        const  pullingInterval = document.getElementById("pullingInterval").value;
        const pullingAddressValue = document.getElementById("pullingAddress").value;
        if (pullingInterval === ''){
            alert('pulling interval cannot be empty');
            return;
        }
        this.setState({
            buttonState: 'loading'
        });
        this.service.verify({value: pullingAddressValue}).then(() => {
            this.setState({buttonState: 'success'});
            this.saveInitPullingData(pullingAddressValue, pullingInterval)
        }).catch((er) => {
            console.log(er);
            alert("Something is wrong with the address");
            this.setState({buttonState: 'error'})
        });
    };

    saveInitPullingData(pullingAddress, pullingInterval) {
        this.service.start({"baseAddress": pullingAddress, "interval": parseInt(pullingInterval) }).then(() => {
            this.setState({
                pullingAddress: pullingAddress,
                getPullingAddress: false
            });
            this.props.savePullingInitData(pullingAddress);
        }).catch((er) => {
            this.setState({buttonState: 'error'});
            console.log('error during startuing pulling data ' + er);
        });
    };

    handleChangePullingAddress() {
        alert('changing pulling address');
        this.setState({
            pullingAddress: '',
            getPullingAddress: true,
            buttonState: ''
        });
    }

    saveClusterAlias() {
        const clusterAliasValue = document.getElementById("clusterAliasValue").value;
        this.setState ({
            clusterAlias: clusterAliasValue
        });
        // send the alias value to backend
    }

    showPullingAddress() {
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
                    <Button type="SaveClusterAlias"
                            className="btn btn-default"
                            onClick={this.saveClusterAlias}
                    >Save Cluster alias
                    </Button>
                    </div>
                        </div>
                <PullingArgumentsComponent/>
            </div>
        )
    }

    getPullingAddress() {
        return (
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
        );
    }

    renderPullingAddress() {
        if (this.state.pullingAddress === '') {
            return this.getPullingAddress();
        } else {
            return this.showPullingAddress();
        }
    }

    render() {
        return (
            <div>
                {this.renderPullingAddress()}
            </div>
        );
    }
}

export default ManageComponent;