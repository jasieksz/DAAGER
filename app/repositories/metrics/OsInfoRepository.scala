package repositories.metrics

import model.domain.metrics.OSInfo
import model.infra.metrics.OSInfos

class OsInfoRepository extends MetricsRepository[OSInfo, OSInfos](OSInfos.query) {

}
