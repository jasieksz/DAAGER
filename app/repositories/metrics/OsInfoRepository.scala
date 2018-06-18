package repositories.metrics

import model.domain.metrics.OSInfo
import model.infra.metrics.OSInfos
import slick.jdbc.PostgresProfile.api._
import utils.DateTimeUtils.dateTime

import scala.concurrent.ExecutionContext

class OsInfoRepository extends MetricsRepository[OSInfo, OSInfos](OSInfos.query) {

  private val byAddressQuery  = Compiled { (address: Rep[String], maxValues: ConstColumn[Long]) =>
    OSInfos.query.filter(_.address === address)
      .sortBy(_.timestamp)
      .take(maxValues)
  }


  def findLastByAddress(address: String)(implicit ec: ExecutionContext): DBIO[Option[OSInfo]] = {
    byAddressQuery(address, 1).result.headOption
  }

}
