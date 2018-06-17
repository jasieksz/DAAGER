package model.domain.metrics

import org.joda.time.DateTime
import play.api.libs.json._
import utils.DateTimeUtils.dateTimeFormat

case class OSInfo(
  timestamp: DateTime,
  address: String,
  osProcessCpuLoad: Double,
  osSystemLoadAverage: Double,
  osSystemCpuLoad: Double,
  osTotalPhysicalMemorySize: Long,
  osFreePhysicalMemorySize: Long,
  osFreeSwapSpaceSize: Long,
  osTotalSwapSpaceSize: Long
) extends Metric

object OSInfo {
  implicit val format = Json.format[OSInfo]
}