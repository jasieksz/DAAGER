package services
import actors.MetricsPuller
import akka.actor.FSM.SubscribeTransitionCallBack
import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Inject
import com.typesafe.config.ConfigFactory
import model.domain.Worker
import model.domain.metrics._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import repositories.metrics._
import scala.collection.JavaConverters._

class WorkersCreationService @Inject()(
  networkInfoRepository: NetworkInfoRepository,
  osInfoRepository: OsInfoRepository,
  runtimeInfoRepository: RuntimeInfoRepository,
  threadInfoRepository: ThreadInfoRepository,
  logEventRepository: LogEventRepository,
  wSClient: WSClient,
  dbConfigProvider: DatabaseConfigProvider
) {

  private val config = ConfigFactory.load()

  private val pullingAddresses = config
    .getObject("daager.observableEndpoints")
    .unwrapped()
    .asInstanceOf[java.util.HashMap[String, String]]
    .asScala

  private val labels = config
    .getObject("daager.observableLabels")
    .unwrapped()
    .asInstanceOf[java.util.HashMap[String, String]]
    .asScala

  def createWorkers(clusterAlias: String, system: ActorSystem, supervisor: ActorRef): Seq[Worker] = {

    val networkInfoPuller = system.actorOf(
      MetricsPuller.props(
        new DataPullingService[NetworkInfo, NetworkInfoRepository](
          networkInfoRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      clusterAlias ++ "_network_info_puller"
    )
    networkInfoPuller ! SubscribeTransitionCallBack(supervisor)

    val osInfoPuller = system.actorOf(
      MetricsPuller.props(
        new DataPullingService[OSInfo, OsInfoRepository](
          osInfoRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      clusterAlias ++ "_os_info_puller"
    )
    osInfoPuller ! SubscribeTransitionCallBack(supervisor)

    val runtimeInfoPuller = system.actorOf(
      MetricsPuller.props(
        new DataPullingService[RuntimeInfo, RuntimeInfoRepository](
          runtimeInfoRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      clusterAlias ++ "_runtime_info_puller"
    )
    runtimeInfoPuller ! SubscribeTransitionCallBack(supervisor)

    val threadInfoPuller = system.actorOf(
      MetricsPuller.props(
        new DataPullingService[ThreadInfo, ThreadInfoRepository](
          threadInfoRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      clusterAlias ++ "_thread_info+puller"
    )
    threadInfoPuller ! SubscribeTransitionCallBack(supervisor)

    val logPuller = system.actorOf(
      MetricsPuller.props(
        new DataPullingService[LogEvent, LogEventRepository](
          logEventRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      clusterAlias ++ "_log_puller"
    )
    logPuller ! SubscribeTransitionCallBack(supervisor)

    Seq(
      Worker(networkInfoPuller, labels("networkInfoPuller"), pullingAddresses("networkInfoPuller")),
      Worker(osInfoPuller, labels("osInfoPuller"), pullingAddresses("osInfoPuller")),
      Worker(runtimeInfoPuller, labels("runtimeInfoPuller"), pullingAddresses("runtimeInfoPuller")),
      Worker(threadInfoPuller, labels("threadInfoPuller"), pullingAddresses("threadInfoPuller")),
      Worker(logPuller, labels("logsPuller"), pullingAddresses("logsPuller"))
    )

  }

}
