import React, { Component } from 'react';
import { Table } from 'reactstrap';
import { Button } from 'reactstrap';

export class PullingArgumentsComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            network: '',
            os: '',
            runtime: '',
            thread: '',
        };

        this.saveIntervalInputs = this.saveIntervalInputs.bind(this);
    }

    handleStopPullingData(id) {
        console.log(id);
    }


    saveIntervalInputs(id) {
        console.log('save interval inputs');
        const network = document.getElementById("network").value;
        const os = document.getElementById("os").value;
        const runtime = document.getElementById("runtime").value;
        const thread = document.getElementById("thread").value;

        console.log(network);
        console.log(os);
        console.log(runtime);
        console.log(thread);
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
                                    onClick={(e) => this.handleStopPullingData(e)}
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
                                   placeholder={this.state.os}
                            />
                        </td>
                        <td>
                            <Button type="primary"
                                    className="btn btn-default"
                                    onClick={(e) => this.handleStopPullingData(e)}
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
                                    onClick={(e) => this.handleStopPullingData(e)}
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
                                    onClick={(e) => this.handleStopPullingData(e)}
                            >Stop
                            </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
                <Button color={"success"}
                        type="saveIntervalInputs"
                        className="btn btn-default"
                        onClick={(e) => this.saveIntervalInputs(e)}
                >Save Intervals
                </Button>
            </div>
        );
    }

    render() {
        return (
            <div>
                {this.renderTableWithIntervals()}
            </div>
    );
    }
}