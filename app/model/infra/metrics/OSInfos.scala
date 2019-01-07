package model.infra.metrics

import model.domain.metrics.OSInfo
import org.joda.time.DateTime
import utils.DaagerPostgresProfile.api._
import utils.DateTimeUtils._

class OSInfos(tag: Tag) extends Table[OSInfo](tag, "os_infos") {

  def timestamp = column[DateTime]("timestamp")

  def clusterId = column[String]("cluster_id")

  def address = column[String]("address")

  def osSystemLoadAverage = column[Double]("os_system_load_average")

  def diskUsableSpace: Rep[Long] = column[Long]("disk_usable_space")

  def diskFreeSpace: Rep[Long] = column[Long]("disk_free_space")

  def diskTotalSpace: Rep[Long] = column[Long]("disk_total_space")

  def * =
    (
      timestamp,
      clusterId,
      address,
      osSystemLoadAverage,
      diskUsableSpace,
      diskFreeSpace,
      diskTotalSpace,
    ) <> ((OSInfo.apply _).tupled, OSInfo.unapply)

}

object OSInfos {
  lazy val query = TableQuery[OSInfos]
}
