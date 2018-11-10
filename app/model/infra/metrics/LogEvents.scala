package model.infra.metrics
import model.domain.metrics.LogEvent
import org.joda.time.DateTime
import utils.DaagerPostgresProfile.api._

class LogEvents(tag: Tag) extends Table[LogEvent](tag, "log_events") {

  def timestamp = column[DateTime]("timestamp")

  def clusterId = column[String]("cluster_id")

  def address = column[String]("address")

  def message = column[String]("message")

  def loggerName = column[String]("logger_name")

  def logLevel = column[String]("log_level")

  def * = (timestamp, clusterId, address, message, loggerName, logLevel) <> (
    (LogEvent.apply _).tupled, LogEvent.unapply
  )

}

object LogEvents {
  lazy val query = TableQuery[LogEvents]
}
