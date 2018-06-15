import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import registerServiceWorker from './registerServiceWorker';
import { App } from "./components/App";

ReactDOM.render(
    <App/>,
    document.getElementById('root')
);

registerServiceWorker();
