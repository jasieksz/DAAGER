package model.domain.metrics

import org.joda.time.DateTime
import play.api.libs.json.Json
import utils.DateTimeUtils.dateTimeFormat

case class NetworkInfo(
  timestamp: DateTime,
  clusterId: String,
  address: String,
  tcpConnectionActiveCount: Long,
  tcpConnectionCount: Long,
  tcpConnectionClientCount: Long
) extends Metric

object NetworkInfo {
  implicit val format = Json.format[NetworkInfo]
}
