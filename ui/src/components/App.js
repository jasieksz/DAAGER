import React, { Component } from 'react';
import MainPage from "./MainPage";

export class App extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <MainPage/>
            </div>
        );
    }
}