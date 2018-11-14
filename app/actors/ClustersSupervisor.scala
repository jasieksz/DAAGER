package actors
import actors.ClustersSupervisor._
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import model.domain.{Cluster, PullerInfo}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import repositories.ClustersRepository
import services.MetricsSupervisorCreationService

object ClustersSupervisor {

  final case class AddCluster(cluster: Cluster, interval: Int)

  final case class RemoveCluster(alias: String)

  final case object ClusterExists

  final case object Done

  final case class GetStatuses(clusterAlias: String)

  final case class UpdateInterval(clusterAlias: String, workerAddress: String, newInterval: Int)
  implicit val updateIntervalFormat = Json.format[UpdateInterval]

  final case class Stop(clusterAlias: String, workerAddress: String)
  implicit val stopFormat = Json.format[Stop]

  def props(
    clustersRepository: ClustersRepository,
    dbConfigProvider: DatabaseConfigProvider,
    metricsSupervisorCreationService: MetricsSupervisorCreationService
  ) = Props(new ClustersSupervisor(clustersRepository, dbConfigProvider, metricsSupervisorCreationService))

}

class ClustersSupervisor(
  clustersRepository: ClustersRepository,
  dbConfigProvider: DatabaseConfigProvider,
  metricsSupervisorCreationService: MetricsSupervisorCreationService
) extends Actor {

  override def receive: Receive = empty

  def empty: Receive = {
    case AddCluster(cluster, interval) =>
      val supervisor = addCluster(cluster, interval)
      context become nonEmptyClusters(Map(cluster.alias -> supervisor))

    case GetStatuses(_) => sender ! Seq.empty[PullerInfo]
  }

  def nonEmptyClusters(clusters: Map[String, ActorRef]): Receive = {

    case AddCluster(cluster, interval) =>
      if (clusters.contains(cluster.alias)) {
        sender ! ClusterExists
      } else {
        val supervisor = addCluster(cluster, interval)
        context become nonEmptyClusters(clusters + (cluster.alias -> supervisor))
      }

    case RemoveCluster(alias) if clusters.contains(alias) =>
      context stop clusters(alias)
      if (clusters.size == 1) context become empty else context become nonEmptyClusters(clusters - alias)

    case GetStatuses(clusterAlias) if clusters.contains(clusterAlias) =>
      clusters(clusterAlias) ! MetricsSupervisor.GetStatus(sender)
    case GetStatuses(_) => Seq.empty[PullerInfo]

    case UpdateInterval(clusterAlias, workerAddress, newInterval) if clusters.contains(clusterAlias) =>
      clusters(clusterAlias) ! MetricsSupervisor.UpdateInterval(workerAddress, newInterval)

    case Stop(clusterAlias, workerAddress) if clusters.contains(clusterAlias) =>
      clusters(clusterAlias) ! MetricsSupervisor.Stop(workerAddress)

  }

  private def addCluster(cluster: Cluster, interval: Int): ActorRef = {
    val supervisor = context.actorOf(
      metricsSupervisorCreationService.getSupervisorProps(cluster.alias),
      cluster.alias + "_metrics_supervisor"
    )
    supervisor ! MetricsSupervisor.Start(cluster.baseAddress, interval)
    sender ! Done
    supervisor
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _ => SupervisorStrategy.Stop
  }

}
