import React, { Component } from 'react';
import EntryScreen from "./EntryScreen";
import CenterView from "./CenterView";

class MainPage extends Component {

    constructor(props) {
        super(props);
        this.state = {showLogin: true};
    }

    _showMain = () => this.setState({showLogin: false});

    _showFailure = () =>  alert("Something is wrong with the address");

    render() {
        return (
            <div>
                {this.state.showLogin ? <CenterView><EntryScreen onSuccess={this._showMain} onFailure={this._showFailure}/></CenterView> : <h1>DAAGER</h1>}
            </div>
        );
    }
}

export default MainPage;