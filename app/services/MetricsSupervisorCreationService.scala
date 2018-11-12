package services
import actors.MetricsSupervisor
import akka.actor.Props
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import repositories.metrics._

class MetricsSupervisorCreationService @Inject()(
  networkInfoRepository: NetworkInfoRepository,
  osInfoRepository: OsInfoRepository,
  runtimeInfoRepository: RuntimeInfoRepository,
  threadInfoRepository: ThreadInfoRepository,
  logEventRepository: LogEventRepository,
  wSClient: WSClient,
  dbConfigProvider: DatabaseConfigProvider
) {

  def getSupervisorProps(clusterAlias: String): Props = {
    MetricsSupervisor.props(
      clusterAlias,
      networkInfoRepository,
      osInfoRepository,
      runtimeInfoRepository,
      threadInfoRepository,
      logEventRepository,
      wSClient,
      dbConfigProvider
    )
  }

}
