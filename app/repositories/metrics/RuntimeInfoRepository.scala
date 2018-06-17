package repositories.metrics

import model.domain.metrics.RuntimeInfo
import model.infra.metrics.RuntimeInfos

class RuntimeInfoRepository extends MetricsRepository [RuntimeInfo, RuntimeInfos](RuntimeInfos.query){

}
