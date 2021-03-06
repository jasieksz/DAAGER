package model.infra.metrics

import model.domain.metrics.RuntimeInfo
import org.joda.time.DateTime
import utils.DaagerPostgresProfile.api._
import utils.DateTimeUtils._

class RuntimeInfos(tag: Tag) extends Table[RuntimeInfo](tag, "runtime_infos") {

  def timestamp = column[DateTime]("timestamp")

  def clusterId = column[String]("cluster_id")

  def address = column[String]("address")

  def runtimeAvailableProcessors = column[Long]("runtime_available_processors")

  def runtimeTotalMemory = column[Long]("runtime_total_memory")

  def runtimeMaxMemory = column[Long]("runtime_max_memory")

  def runtimeFreeMemory = column[Long]("runtime_free_memory")

  def runtimeUsedMemory = column[Long]("runtime_used_memory")

  def * =
    (
      timestamp,
      clusterId,
      address,
      runtimeAvailableProcessors,
      runtimeTotalMemory,
      runtimeMaxMemory,
      runtimeFreeMemory,
      runtimeUsedMemory
    ) <> ((RuntimeInfo.apply _).tupled, RuntimeInfo.unapply)

}

object RuntimeInfos {
  lazy val query = TableQuery[RuntimeInfos]
}
