package model.infra.metrics

import model.domain.metrics.ThreadInfo
import model.infra.metrics.NetworkInfos.query
import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._
import utils.DateTimeUtils._

class ThreadInfos(tag: Tag) extends Table[ThreadInfo](tag, "thread_infos") {

  def timestamp = column[DateTime]("timestamp")

  def address = column[String]("address")

  def threadPeakThreadCount = column[Long]("thread_peak_count")

  def threadThreadCount = column[Long]("thread_thread_count")

  def * = (timestamp, address, threadPeakThreadCount, threadThreadCount) <> (
    (ThreadInfo.apply _).tupled, ThreadInfo.unapply
  )

}

object ThreadInfos{
  lazy val query = TableQuery[ThreadInfos]
}