import axios from 'axios';

class ApiService {

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

    getGraph = () => this.api.get("/api/graph", {timeout: 10000});

    start = (obj) => this.api.post("/api/pull/start", obj);

    getStatuses = () => this.api.get("api/pull/statuses");

    updateInterval = (obj) => this.api.post("api/pull/update", obj);

    stopPullingParam = (obj) => this.api.post("/api/pull/stop", obj);

    getNodeDetailInfo = (obj) => this.api.post("/api/node-details", (obj));

    getGloalState = () => this.api.get("api/global-state");
}

export default ApiService;