import actors.ClustersSupervisor
import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import javax.inject.Singleton
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import repositories.ClustersRepository
import services.MetricsSupervisorCreationService

class Module extends AbstractModule {

  override def configure(): Unit =
    bind(classOf[ApplicationLoader]).asEagerSingleton()

  @Provides
  @Singleton
  @Named("ClustersSupervisor")
  def getClustersSupervisor(
    actorSystem: ActorSystem,
    clustersRepository: ClustersRepository,
    metricsSupervisorCreationService: MetricsSupervisorCreationService,
    wSClient: WSClient,
    dbConfigProvider: DatabaseConfigProvider
  ): ActorRef = {
    actorSystem.actorOf(
      ClustersSupervisor.props(
        clustersRepository,
        dbConfigProvider,
        metricsSupervisorCreationService
      ),
      "cluster_supervisor"
    )
  }

}
