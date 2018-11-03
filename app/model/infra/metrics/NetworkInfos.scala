package model.infra.metrics

import model.domain.metrics.NetworkInfo
import org.joda.time.DateTime
import utils.DaagerPostgresProfile.api._
import utils.DateTimeUtils._

class NetworkInfos(tag: Tag) extends Table[NetworkInfo](tag, "network_infos") {

  def timestamp = column[DateTime]("timestamp")

  def clusterId = column[String]("cluster_id")

  def address = column[String]("address")

  def tcpConnectionActiveCount = column[Long]("tcp_connection_active_count")

  def tcpConnectionCount = column[Long]("tcp_connection_count")

  def tcpConnectionClientCount = column[Long]("tcp_connection_client_count")

  def * =
    (timestamp, clusterId, address, tcpConnectionActiveCount, tcpConnectionClientCount, tcpConnectionCount) <> (
      (NetworkInfo.apply _).tupled, NetworkInfo.unapply
    )

}

object NetworkInfos {
  lazy val query = TableQuery[NetworkInfos]
}
