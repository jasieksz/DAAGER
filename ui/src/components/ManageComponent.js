import React, { Component } from 'react';
import '../styles/ManageComponent.css';
import { Button } from 'reactstrap';
import ProgressButton from 'react-progress-button'
import "../../node_modules/react-progress-button/react-progress-button.css";
import EntryScreenService from "../services/EntryScreenService"
import { PullingArgumentsComponent } from "./PullingArgumentsComponent";
class ManageComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            pullingAddress: this.props.pullingAddress,
            isPulling: false,
            getPullingAddress: true,
            buttonState: ''
        };
        this.service = new EntryScreenService();
        this.savePullingAddress = this.savePullingAddress.bind(this);
        this.handleClearPullingAddress = this.handleClearPullingAddress.bind(this);

        this.handleChangePullingAddress = this.handleChangePullingAddress.bind(this);
        this.showPullingAddress = this.showPullingAddress.bind(this);
    }

    componentDidMount() {
        this.service.hello().then(response => console.log(response));
    }


    handleSubmitManageInputs(event) {
        console.log(event);
    }

    handleStopPullingData() {
        console.log('start/stop pulling data');
    }

    handleClearPullingAddress() {
        console.log('clean');
    }

    verifyPullingAddress = () => {
        //const  = document.getElementById("pullingInterval").value;
        const pullingAddressValue = document.getElementById("pullingAddress").value;
        console.log(pullingAddressValue);
        // if (pullingInterval === ''){
        //     alert('pulling interval cannot be empty');
        //     return;
        // }
        this.setState({
            buttonState: 'loading'
        });
        this.service.verify({value: pullingAddressValue}).then(() => {
            this.setState({buttonState: 'success'});
            this.savePullingAddress(pullingAddressValue)
        }).catch((er) => {
            console.log(er);
            alert("Something is wrong with the address");
            this.setState({buttonState: 'error'})
        });
    };

    savePullingAddress(pullingAddress) {
        this.setState({
            pullingAddress: pullingAddress,
            getPullingAddress: false
        });
        this.props.savePullingAddress(pullingAddress);
    }

    handleChangePullingAddress() {
        alert('changing pulling address');
        console.log('change pulling address');
        this.setState({
            pullingAddress: '',
            getPullingAddress: true,
            buttonState: ''
        });
        this.props.savePullingAddress('');
    }


    showPullingAddress() {
        return (
            <div>
            <div className={'pullingDataInfo'}>
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
                            onClick={this.handleClearPullingAddress}
                    >Clear Pulling Address
                    </Button>
                    <Button color="warning"
                            className="btn btn-default manageButton"
                            onClick={this.handleChangePullingAddress}
                    >Change Pulling Address
                    </Button>
                    <Button type="StopPullingData"
                            className="btn btn-default"
                            onClick={this.handleStopPullingData}
                    >Stop
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
                <h2 className={'tabTitle'}>Manage Component </h2>
                {this.renderPullingAddress()}
            </div>
        );
    }
}

export default ManageComponent;