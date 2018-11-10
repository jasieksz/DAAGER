import React, { Component } from 'react';
import { Table } from 'reactstrap';
import { Button } from 'reactstrap';
import ApiService from "../services/ApiService";
import _ from 'lodash';
export class PullingArgumentsComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            statuses: [],
            isPulling: true,
        };
        this.service = new ApiService();
        this.getStatuses();
    }

    handleStopPullingData = (id) => {
        this.service.stopPullingParam(this.createStopPullingData(id.address)).then(response => {
            this.getStatuses();
        }).catch((err) => {
            console.log('error during updating statuses data, address:' + id.address);
            console.log(err);
        });
    };

    handleContinuePullingData = (id) => {
        const a = _.filter(this.state.statuses, i => i.address === id.address);
        this.handleUpdateInterval(a[0].address, a[0].interval);
    };


    saveIntervalInputs = (id) =>{
        const value = document.getElementById(id.address).value;
        if (value === '') {
            alert('enter interval value');
            return;
        }
        this.handleUpdateInterval(id.address, value);
    };

    handleUpdateInterval = (address, value) => {
        this.service.updateInterval(this.createIntervalUpdate(address, parseInt(value))).then( () =>  {
            this.getStatuses();
        }).catch((err) => {
            console.log('error during updating statuses data' + address);
            console.log(err);
        });
    };

    createIntervalUpdate(address, interval) {
        return {
            "workerAddress": address,
            "newInterval": interval
        }
    }

    createStopPullingData(address) {
        return {
            "workerAddress": address
        }
    }

    getStatuses = () => {
        this.service.getStatuses().then(response => {
            this.setState({
                statuses: response.data
            });
            this.createTable();
        }).catch(() => {
            console.log('error durig getting statuses data');
        });
    };

    createTable = () => {
      return _.map(this.state.statuses,  i =>
          <tr>
              <th scope="row">{i.label}</th>
              <td> {i.status} </td>
              <td>
                  <input type="number"
                         className="form-control"
                         id={i.address}
                         placeholder={i.interval}
                  />
              </td>
              <td>
                  <Button color={"success"}
                          className="btn btn-default"
                          onClick={ () => this.saveIntervalInputs(i) }
                  >Save Interval
                  </Button>
              </td>
              <td>
                  {this.getStatusButton(i)}
              </td>
          </tr>);
    };

    getStatusButton(id) {
        if (id.status === 'Pulling') {
            return (
                <Button type="danger"
                    className="btn btn-default"
                    onClick={ () => this.handleStopPullingData(id) }
                >Stop
                </Button>
            );
        } else {
            return (
                <Button type="success"
                    className="btn btn-default"
                    onClick={ () => this.handleContinuePullingData(id) }
                >Start
                </Button>
            );
        }
    }

    handleAllStopPullingData = () => {
        this.state.statuses.forEach(i => this.handleStopPullingData(i));
        this.changeHealthPullingButton();
    };


    handleAllStartPullingData = () => {
        this.state.statuses.forEach(i => this.handleContinuePullingData(i));
        this.changeHealthPullingButton();
    };

    changeHealthPullingButton = () => {
        this.setState({
            isPulling: !this.state.isPulling
        });
    };

    getHealthStatusButton =() =>  {
        if (this.state.isPulling) {
            return (
                <Button color={"danger"}
                        className="btn btn-default"
                        onClick={ () => this.handleAllStopPullingData() }
                >Stop All
                </Button>
            );
        } else {
            return (
                <Button color={"success"}
                        className="btn btn-default"
                        onClick={ () => this.handleAllStartPullingData() }
                >Start All
                </Button>
            );
        }
    };

    renderTableWithIntervals =() => {
        return (
            <div className={'inputForm'}>
                <Table>
                    <thead>
                    <tr>
                        <th>Parameter</th>
                        <th>Status</th>
                        <th>Interval</th>
                        <th>Save</th>
                        <th>Stop</th>
                    </tr>
                    </thead>
                    <tbody>
                        {this.createTable()}
                    </tbody>
                </Table>
                {this.getHealthStatusButton()}
            </div>
        );
    };

    render() {
        if (this.state.statuses !== []) {
            return (
                <div>
                    {this.renderTableWithIntervals()}
                </div>
            );
        } else {
            return <div/>;
        }
    }
}