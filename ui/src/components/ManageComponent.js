import React, { Component } from 'react';
import '../styles/ManageComponent.css';
import { Button } from 'reactstrap';
import { Table } from 'reactstrap';
import ProgressButton from 'react-progress-button'
import "../../node_modules/react-progress-button/react-progress-button.css";
import EntryScreenService from "../services/EntryScreenService"
class ManageComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            network: '',
            os: '',
            runtime: '',
            thread: '',
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
            getPullingAddress: true
        });
    }

    renderTableWithIntervals() {
        return (
            <div className={'inputForm'}>
                <Table>
                    <thead>
                        <tr>
                            <th>Parameter</th>
                            <th>Value</th>
                            <th>Stop</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <th scope="row">Network</th>
                                <td>
                                    <input type="number"
                                           className="form-control"
                                           id="network"
                                           placeholder={this.state.network}
                                    />
                                </td>
                            <td>
                                <Button type="primary"
                                        className="btn btn-default"
                                        onClick={this.handleStopPullingData()}
                                >Stop
                                </Button>
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">Operating system</th>
                                <td>
                                    <input type="number"
                                           className="form-control"
                                           id="os"
                                           value={this.state.os}
                                    />
                                </td>
                            <td>
                            <Button type="primary"
                                    className="btn btn-default"
                                    onClick={this.handleStopPullingData}
                            >Stop
                            </Button>
                        </td>
                        </tr>
                        <tr>
                            <th scope="row">Runtime</th>
                                <td>
                                    <input type="text"
                                           id="runtime"
                                           className="form-control"
                                           placeholder={this.state.runtime}
                                    />
                                </td>
                            <td>
                            <Button type="StopPullingData"
                                    className="btn btn-default"
                                    onClick={this.handleStopPullingData}
                            >Stop
                            </Button>
                        </td>
                        </tr>
                        <tr>
                            <th scope="row">Thread</th>
                                <td>
                                    <input type="text"
                                           className="form-control"
                                           id="thread"
                                           placeholder={this.state.thread}
                                    />
                                </td>
                            <td>
                            <Button type="StopPullingData"
                                    className="btn btn-default"
                                    onClick={this.handleStopPullingData}
                            >Stop
                            </Button>
                        </td>
                        </tr>
                    </tbody>
                </Table>
            </div>
        );
    }

    showPullingAddress() {
        return (
            <div>
            <div className={'inputForm'}>
                <div className="form-group row">
                    <label htmlFor="pullingAddress" className="col-sm-4 col-form-label">Pulling data address</label>
                    <div className="col-sm-8">
                        <input type="text"
                               className="form-control"
                               value={this.state.pullingAddress}
                        />
                    </div>
                </div>
                <div className={'manageButtons'}>
                    <Button color="warning"
                            className="btn btn-default"
                            onClick={this.handleClearPullingAddress}
                    >Clear Pulling Address
                    </Button>
                    <Button color="warning"
                            className="btn btn-default"
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
                {this.renderTableWithIntervals()}
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
        if (this.props.pullingAddress === '') {
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