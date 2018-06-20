package repositories.metrics

import model.domain.metrics.RuntimeInfo
import model.infra.metrics.RuntimeInfos
import slick.jdbc.PostgresProfile.api._

sealed trait RuntimeInfoQueries {

  protected lazy val byAddress = Compiled { (address: Rep[String], maxResults: ConstColumn[Long]) =>
    RuntimeInfos.query.filter(_.address === address).take(maxResults)
  }

}

class RuntimeInfoRepository extends MetricsRepository[RuntimeInfo, RuntimeInfos](RuntimeInfos.query) with RuntimeInfoQueries {

  def findByAddress(address: String, maxResults: Int = 100): DBIO[Seq[RuntimeInfo]] = {
    byAddress((address, maxResults)).result
  }

}
