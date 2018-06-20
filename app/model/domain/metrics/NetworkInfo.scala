package model.domain.metrics

import org.joda.time.DateTime
import play.api.libs.json._
import utils.DateTimeUtils.dateTimeFormat

case class NetworkInfo(
  date: DateTime,
  address: String,
  tcpConnectionActiveCount: Long,
  tcpConnectionCount: Long,
  tcpConnectionClientCount: Long
) extends Metric

object NetworkInfo {
  implicit val format = Json.format[NetworkInfo]
}
