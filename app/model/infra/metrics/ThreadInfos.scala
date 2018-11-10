package model.infra.metrics

import model.domain.metrics.ThreadInfo
import org.joda.time.DateTime
import utils.DaagerPostgresProfile.api._
import utils.DateTimeUtils._

class ThreadInfos(tag: Tag) extends Table[ThreadInfo](tag, "thread_infos") {

  def timestamp = column[DateTime]("timestamp")

  def clusterId = column[String]("cluster_id")

  def address = column[String]("address")

  def threadPeakThreadCount = column[Long]("thread_peak_count")

  def threadThreadCount = column[Long]("thread_thread_count")

  def threadDaemonThreadCount: Rep[Long] = column[Long]("thread_daemon_thread_count")

  def threadTotalStartedThreadCount: Rep[Long] = column[Long]("thread_total_started_thread_count")

  def * =
    (
      timestamp,
      clusterId,
      address,
      threadPeakThreadCount,
      threadThreadCount,
      threadDaemonThreadCount,
      threadTotalStartedThreadCount
    ) <> (
      (ThreadInfo.apply _).tupled, ThreadInfo.unapply
    )

}

object ThreadInfos {
  lazy val query = TableQuery[ThreadInfos]
}
