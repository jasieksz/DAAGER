import axios from 'axios';

class EntryScreenService {

    constructor() {
        this.api = axios.create({
            timeout: 2000,
            withCredentials: true,
            xsrfCookieName: "DAAGER-TOKEN",
            xsrfHeaderName: "Csrf-Token",
        });
    }

    hello = () => this.api.get("api/hello");

    verify = (obj) => this.api.post("api/pull/verify", obj, {timeout: 10000});

    startPulling = (obj) => this.api.post("api/pull/start", obj, {timeout: 10000});

}

export default EntryScreenService;