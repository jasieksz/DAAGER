package model.infra.metrics

import model.domain.metrics.RuntimeInfo
import model.infra.metrics.NetworkInfos.query
import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._
import utils.DateTimeUtils._

class RuntimeInfos(tag: Tag) extends Table[RuntimeInfo](tag, "runtime_infos"){

  def timestamp = column[DateTime]("timestamp")

  def address = column[String]("address")

  def runtimeAvailableProcessors = column[Long]("runtime_available_processors")

  def runtimeTotalMemory = column[Long]("runtime_total_memory")

  def runtimeMaxMemory = column[Long]("runtime_max_memory")

  def runtimeFreeMemory = column[Long]("runtime_free_memory")

  def runtimeUsedMemory = column[Long]("runtime_used_memory")


  def * = (timestamp, address, runtimeAvailableProcessors, runtimeTotalMemory, runtimeMaxMemory, runtimeFreeMemory,
    runtimeUsedMemory) <> ((RuntimeInfo.apply _).tupled, RuntimeInfo.unapply)

}

object RuntimeInfos {
  lazy val query = TableQuery[RuntimeInfos]
}
