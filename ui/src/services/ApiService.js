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

    getGraph = (clusterAlias) => this.api.get("/api/graph/" + clusterAlias, {timeout: 10000});

    start = (obj) => this.api.post("/api/pull/start", obj);

    getStatuses = (clusterAlias) => this.api.get("api/pull/statuses/" + clusterAlias);

    updateInterval = (obj) => this.api.post("api/pull/update", obj);

    stopPullingParam = (obj) => this.api.post("/api/pull/stop", obj);

    getNodeDetailInfo = (obj) => this.api.post("/api/node-details", (obj));

    getGlobalState = (clusterAlias) => this.api.get("api/global-state/" + clusterAlias);

    getAllClusters = () => this.api.get("/api/clusters", {timeout: 10000});

    deleteCluster = (clusterAlias) => this.api.delete("/api/clusters/" + clusterAlias);
}

export default ApiService;