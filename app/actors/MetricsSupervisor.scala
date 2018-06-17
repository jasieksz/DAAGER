package actors


import actors.MetricsPuller.StartPulling
import actors.MetricsSupervisor.Start
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{ Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy }
import model.domain.metrics.{ NetworkInfo, OSInfo, RuntimeInfo, ThreadInfo }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import repositories.metrics.{ NetworkInfoRepository, OsInfoRepository, RuntimeInfoRepository, ThreadInfoRepository }

object MetricsSupervisor {
  case class Start(baseAddress: String, interval: Int)
}

class MetricsSupervisor(
  networkInfoRepository: NetworkInfoRepository,
  osInfoRepository: OsInfoRepository,
  runtimeInfoRepository: RuntimeInfoRepository,
  threadInfoRepository: ThreadInfoRepository,
  wSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
)extends Actor {

  private val (networkInfoPuller,osInfoPuller,runtimeInfoPuller,threadInfoPuller) = createWorkers()

  override def receive: Receive = {
    case Start(baseAddress, interval) =>
      networkInfoPuller ! StartPulling(baseAddress + "/api/mock/network", interval)
      osInfoPuller ! StartPulling(baseAddress + "/api/mock/os", interval)
      runtimeInfoPuller ! StartPulling(baseAddress + "/api/mock/runtime", interval)
      threadInfoPuller ! StartPulling(baseAddress + "/api/mock/thread", interval)
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(){
    case _ => Restart
  }

  private def createWorkers(): (ActorRef, ActorRef, ActorRef, ActorRef) = {

    val networkInfoPuller = context.system.actorOf(
        Props(new MetricsPuller[NetworkInfo, NetworkInfoRepository](networkInfoRepository, wSClient, dbConfigProvider)),
        "networkInfoPuller")

    val osInfoPuller = context.system.actorOf(
        Props(new MetricsPuller[OSInfo, OsInfoRepository](osInfoRepository, wSClient, dbConfigProvider)),
        "osInfoPuller")

    val runtimeInfoPuller = context.system.actorOf(
        Props(new MetricsPuller[RuntimeInfo, RuntimeInfoRepository](runtimeInfoRepository, wSClient, dbConfigProvider)),
        "runtimeInfoPuller")

    val threadInfoPuller = context.system.actorOf(
        Props(new MetricsPuller[ThreadInfo, ThreadInfoRepository](threadInfoRepository, wSClient, dbConfigProvider)),
        "threadInfoPuller")

    (networkInfoPuller,osInfoPuller,runtimeInfoPuller,threadInfoPuller)
  }

}
