package repositories.metrics

import model.domain.metrics.ThreadInfo
import model.infra.metrics.ThreadInfos

class ThreadInfoRepository extends MetricsRepository[ThreadInfo, ThreadInfos](ThreadInfos.query) {

}
