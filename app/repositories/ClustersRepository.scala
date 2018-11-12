package repositories
import cats.data.OptionT
import model.domain.Cluster
import model.infra.Clusters
import utils.DaagerPostgresProfile.api._

class ClustersRepository {

  private val query = Clusters.query

  def findAll(): DBIO[Seq[Cluster]] =
    query.result

  def findActive(): DBIO[Seq[Cluster]] =
    query.filter(_.isActive).result

  def findById(clusterId: String): OptionT[DBIO, Cluster] =
    OptionT[DBIO, Cluster](query.filter(_.clusterId === clusterId).result.headOption)

  def findExistingByAlias(clusterAlias: String): DBIO[Cluster] = query.filter(_.alias === clusterAlias).result.head

  def exists(clusterId: String, alias: String): DBIO[Boolean] =
    query.filter(cluster => cluster.clusterId === clusterId || cluster.alias === clusterId).exists.result

  def markInactiveByAlias(alias: String): DBIO[Unit] =
    query.filter(_.alias === alias).map(_.isActive).update(false) >> DBIO.successful(())

  def save(cluster: Cluster): DBIO[Unit] =
    (query += cluster) >> DBIO.successful(())

}
