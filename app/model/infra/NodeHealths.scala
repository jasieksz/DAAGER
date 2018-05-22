package model.infra

import model.domain.NodeHealth
import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._
import utils.DateTime._


class NodeHealths(tag: Tag) extends Table[NodeHealth](tag, "node_healths")  {

  def nodeState = column[String]("node_state")

  def clusterState = column[String]("cluster_state")

  def clusterSafe = column[Boolean]("cluster_safe")

  def migrationQueueSize = column[Int]("migration_queue_size")

  def clusterSize = column[Int]("cluster_size")

  def timestamp = column[DateTime]("timestamp")

  def nodeId = column[String]("node_id")

  def pk = primaryKey("node_health_pk", (timestamp, nodeId))

  override def * = (nodeState, clusterState, clusterSafe, migrationQueueSize,
    clusterSize, timestamp, nodeId) <> (NodeHealth.tupled, NodeHealth.unapply)

}

object NodeHealths{
  lazy val query = TableQuery[NodeHealths]
}