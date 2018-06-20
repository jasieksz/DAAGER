package utils

import java.sql.Timestamp

import org.joda.time.DateTime
import play.api.libs.json._
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._

object DateTimeUtils {

  implicit def dateTime: JdbcType[DateTime] with BaseTypedType[DateTime] =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime)
    )

  implicit val dateTimeFormat = new Format[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = JodaReads.jodaDateReads("yyyy-MM-dd HH:mm:ss").reads(json)

    override def writes(o: DateTime): JsValue = JodaWrites.JodaDateTimeWrites.writes(o)
  }

}
