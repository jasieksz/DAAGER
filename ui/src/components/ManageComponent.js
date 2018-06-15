import React, { Component } from 'react';
import '../styles/ManageComponent.css';

class ManageComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            valueInput1: '',
            valueInput2: '',
            valueInput3: ''
        }
    }

    handleSubmitManageInputs(event) {
        console.log(event);
    }


    render() {
        return (
            <div>
                <h2 className={'tabTitle'}>MANAGE COMPONENT </h2>
                <div className={'inputForm'}>
                    <form className="navbar-form navbar-left">
                        <div className="form-group row">
                            <label htmlFor="value1" className="col-sm-2 col-form-label">Value1</label>
                            <div className="col-sm-10">
                                <input type="text" className="form-control" id="value1" placeholder="value1"/>
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="value2" className="col-sm-2 col-form-label">Value 2</label>
                            <div className="col-sm-10">
                                <input type="text" className="form-control" id="value2" placeholder="value2"/>
                            </div>
                        </div>
                        <div className="form-group row">
                            <label htmlFor="value3" className="col-sm-2 col-form-label">Value3</label>
                            <div className="col-sm-10">
                                <input type="text" className="form-control" id="value3" placeholder="value3"/>
                            </div>
                        </div>
                        <button type="submit"
                                className="btn btn-default"
                                onChange={this.handleSubmitManageInputs}
                        >Submit
                        </button>
                    </form>
                </div>
            </div>
        );
    }
}

export default ManageComponent;