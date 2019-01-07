package model.domain.metrics

import org.joda.time.DateTime
import play.api.libs.json.Json
import utils.DateTimeUtils.dateTimeFormat

case class ThreadInfo(
  timestamp: DateTime,
  clusterId: String,
  address: String,
  threadPeakThreadCount: Long,
  threadThreadCount: Long,
  threadDaemonThreadCount: Long,
  threadTotalStartedThreadCount: Long
) extends Metric

object ThreadInfo {
  implicit val format = Json.format[ThreadInfo]
}
