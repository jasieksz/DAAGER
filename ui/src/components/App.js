import React, { Component } from 'react';
import MainPage from "./MainPage";

export class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            showLogin: true,
        };
    }

    _showMain = () => this.setState({showLogin: false});

    _showFailure = () =>  alert("Something is wrong with the address");

    render() {
        return (
            <div>
                {/*{this.state.showLogin ? <CenterView><EntryScreen onSuccess={this._showMain} onFailure={this._showFailure}/></CenterView> : <MainPage/>}*/}
                <MainPage/>
            </div>
        );
    }
}