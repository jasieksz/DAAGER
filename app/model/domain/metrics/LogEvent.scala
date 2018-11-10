package model.domain.metrics
import org.joda.time.DateTime
import play.api.libs.json._
import LogEvent._

case class LogEvent(
  timestamp: DateTime,
  clusterId: String,
  address: String,
  message: String,
  loggerName: String,
  logLevel: String
) extends Metric

object LogEvent {

  private implicit val dateTimeReads = new Reads[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] =
      JsSuccess(new DateTime(json.as[Long]))
  }

  implicit val reads = Json.reads[LogEvent]
}
