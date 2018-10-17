package utils

import org.joda.time.DateTime
import play.api.libs.json._

object DateTimeUtils {

  implicit val dateTimeFormat: Format[DateTime] = new Format[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] =
      JsSuccess(new DateTime(json.as[Long] * 1000))

    override def writes(o: DateTime): JsValue =
      JodaWrites.JodaDateTimeWrites.writes(o)
  }

}
