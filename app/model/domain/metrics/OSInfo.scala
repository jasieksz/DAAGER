package model.domain.metrics

import org.joda.time.DateTime
import play.api.libs.json._
import utils.DateTimeUtils.dateTimeFormat

case class OSInfo(
  timestamp: DateTime,
  clusterId: String,
  address: String,
  osSystemLoadAverage: Double,
  diskUsableSpace: Long,
  diskFreeSpace: Long,
  diskTotalSpace: Long
) extends Metric

object OSInfo {
  implicit val format = Json.format[OSInfo]
}
