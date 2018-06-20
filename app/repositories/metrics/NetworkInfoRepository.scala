package repositories.metrics

import model.domain.metrics.NetworkInfo
import model.infra.metrics.NetworkInfos

class NetworkInfoRepository extends MetricsRepository[NetworkInfo, NetworkInfos](NetworkInfos.query) {

}
