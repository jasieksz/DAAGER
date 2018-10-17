package model.domain

import org.joda.time.DateTime
import play.api.libs.json.Json
import utils.DateTimeUtils.dateTimeFormat

case class NodeDetails(
  address: String,
  lastMsg: Option[DateTime],
  cpu: Double,
  memory: Double
)

object NodeDetails {
  implicit val format = Json.format[NodeDetails]
}
