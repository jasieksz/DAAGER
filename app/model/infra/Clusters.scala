package model.infra
import model.domain.Cluster
import slick.lifted.Tag
import utils.DaagerPostgresProfile.api._

class Clusters(tag: Tag) extends Table[Cluster](tag, "clusters") {
  def clusterId = column[String]("cluster_id")

  def alias = column[String]("alias")

  def baseAddress = column[String]("base_address")

  def isActive = column[Boolean]("is_active")

  def * = (clusterId, alias, baseAddress, isActive) <> ((Cluster.apply _).tupled, Cluster.unapply)
}

object Clusters {
  lazy val query = TableQuery[Clusters]
}
