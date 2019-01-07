import actors.ClustersSupervisor
import akka.actor.ActorRef
import javax.inject.{Inject, Named, Singleton}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ClustersRepository
import utils.DaagerPostgresProfile

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ApplicationLoader @Inject()(
  @Named("ClustersSupervisor")
  clustersSupervisor: ActorRef,
  clustersRepository: ClustersRepository,
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[DaagerPostgresProfile] {

  private val log              = Logger(getClass)
  private val DEFAULT_INTERVAL = 2

  onStart()

  def onStart(): Unit = {
    log.info("Starting application, restoring clusters")
    db.run(clustersRepository.findActive())
      .map(clusters => {
        log.info(s"Found ${clusters.size} clusters")
        clusters.foreach(cluster => clustersSupervisor ! ClustersSupervisor.AddCluster(cluster, DEFAULT_INTERVAL))
      })
  }

}
