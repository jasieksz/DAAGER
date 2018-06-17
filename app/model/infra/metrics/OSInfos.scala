package model.infra.metrics

import model.domain.metrics.OSInfo
import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._
import utils.DateTimeUtils._

class OSInfos(tag: Tag) extends Table[OSInfo](tag, "os_infos") {

  def timestamp = column[DateTime]("timestamp")

  def address = column[String]("address")

  def osProcessCpuLoad = column[Double]("os_process_cpu_load")

  def osSystemLoadAverage = column[Double]("os_system_load_average")

  def osSystemCpuLoad = column[Double]("os_system_cpu_load")

  def osTotalPhysicalMemorySize = column[Long]("os_total_physical_memory_size")

  def osFreePhysicalMemorySize = column[Long]("os_free_physical_memory_size")

  def osFreeSwapSpaceSize = column[Long]("os_free_swap_space_size")

  def osTotalSwapSpaceSize = column[Long]("os_total_swap_space_size")

  def * = (timestamp, address, osProcessCpuLoad, osSystemLoadAverage, osSystemCpuLoad,
    osTotalPhysicalMemorySize, osFreePhysicalMemorySize, osFreeSwapSpaceSize,
    osTotalSwapSpaceSize) <> ((OSInfo.apply _).tupled, OSInfo.unapply)

}

object OSInfos {
  lazy val query = TableQuery[OSInfos]
}
