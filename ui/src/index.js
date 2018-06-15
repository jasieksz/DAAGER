import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import registerServiceWorker from './registerServiceWorker';
import MainPage from "./MainPage";

ReactDOM.render(
    <MainPage/>,
    document.getElementById('root')
);

registerServiceWorker();
