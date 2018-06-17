package model.domain.metrics

import org.joda.time.DateTime
import play.api.libs.json._
import utils.DateTimeUtils.dateTimeFormat

case class RuntimeInfo(
  timestamp: DateTime, //ISO Format
  address: String,
  runtimeAvailableProcessors: Long,
  runtimeTotalMemory: Long,
  runtimeMaxMemory: Long,
  runtimeFreeMemory: Long,
  runtimeUsedMemory: Long
) extends Metric

object RuntimeInfo {
  implicit val format = Json.format[RuntimeInfo]
}


