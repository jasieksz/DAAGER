package actors

import actors.MetricsPuller.StartPulling
import actors.MetricsSupervisor._
import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import model.domain.PullerInfo
import model.domain.metrics.NetworkInfo._
import model.domain.metrics.OSInfo._
import model.domain.metrics.RuntimeInfo._
import model.domain.metrics.ThreadInfo._
import model.domain.metrics._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import repositories.metrics._

object MetricsSupervisor {

  final case class Start(baseAddress: String, interval: Int)

  final case class GetStatus(receiver: ActorRef)

  final case object GetConfig

  final case class UpdateInterval(workerAddress: String, newInterval: Int)

  final case class Stop(workerAddress: String)

  def props(
    clusterAlias: String,
    networkInfoRepository: NetworkInfoRepository,
    osInfoRepository: OsInfoRepository,
    runtimeInfoRepository: RuntimeInfoRepository,
    threadInfoRepository: ThreadInfoRepository,
    logEventRepository: LogEventRepository,
    wSClient: WSClient,
    dbConfigProvider: DatabaseConfigProvider
  ) = Props(
    new MetricsSupervisor(
      clusterAlias,
      networkInfoRepository,
      osInfoRepository,
      runtimeInfoRepository,
      threadInfoRepository,
      logEventRepository,
      wSClient,
      dbConfigProvider
    )
  )

}

class MetricsSupervisor(
  clusterAlias: String,
  networkInfoRepository: NetworkInfoRepository,
  osInfoRepository: OsInfoRepository,
  runtimeInfoRepository: RuntimeInfoRepository,
  threadInfoRepository: ThreadInfoRepository,
  logEventRepository: LogEventRepository,
  wSClient: WSClient,
  dbConfigProvider: DatabaseConfigProvider
) extends Actor {

  private val workers @ Seq(
    networkInfoPuller,
    osInfoPuller,
    runtimeInfoPuller,
    threadInfoPuller,
    logsPuller
  ) = createWorkers()

  //  TODO move to config
  //  TODO make what to pull configurable
  private val pullingAdresses = Map(
    networkInfoPuller -> "/health/tcp",
    osInfoPuller      -> "/health/os",
    runtimeInfoPuller -> "/health/runtime",
    threadInfoPuller  -> "/health/thread",
    logsPuller        -> "/logs"
  )

  private val labels = Map(
    networkInfoPuller -> "Network Info",
    osInfoPuller      -> "OS Info",
    runtimeInfoPuller -> "Runtime Info",
    threadInfoPuller  -> "Thread Info",
    logsPuller        -> "Logs"
  )

  private val addressToWorker = pullingAdresses.map(_.swap)

  private val intervals = Map.empty[ActorRef, Int].withDefault(_ => 0)

  private val actorStates = Map.empty[ActorRef, String].withDefault(_ => "Idle")

  override def receive: Receive = onMessage(actorStates, intervals, "----")

  private def onMessage(
    actorStates: Map[ActorRef, String],
    intervals: Map[ActorRef, Int],
    pullingAddress: String
  ): Receive = {
    case Start(baseAddress, interval) =>
      networkInfoPuller ! StartPulling(
        baseAddress + pullingAdresses(networkInfoPuller),
        interval
      )
      osInfoPuller ! StartPulling(
        baseAddress + pullingAdresses(osInfoPuller),
        interval
      )
      runtimeInfoPuller ! StartPulling(
        baseAddress + pullingAdresses(runtimeInfoPuller),
        interval
      )
      threadInfoPuller ! StartPulling(
        baseAddress + pullingAdresses(threadInfoPuller),
        interval
      )
      logsPuller ! StartPulling(
        baseAddress + pullingAdresses(logsPuller),
        interval
      )
      context.become(
        onMessage(
          actorStates,
          generateIntervals(workers, interval),
          baseAddress
        )
      )
    case CurrentState(worker, state) =>
      context.become(
        onMessage(
          actorStates + (worker -> state.toString),
          intervals,
          pullingAddress
        )
      )
    case Transition(worker, _, newState) =>
      context.become(
        onMessage(
          actorStates + (worker -> newState.toString),
          intervals,
          pullingAddress
        )
      )
    case GetStatus(receiver) =>
      receiver ! prepareStatuses(workers, actorStates, intervals)
    case GetConfig =>
      sender ! pullingAddress
    case UpdateInterval(address, newInterval) =>
      addressToWorker(address) ! MetricsPuller.ChangeInterval(newInterval)
      context.become(
        onMessage(
          actorStates,
          intervals + (addressToWorker(address) -> newInterval),
          pullingAddress
        )
      )
    case Stop(address) =>
      addressToWorker(address) ! MetricsPuller.Stop
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 10 seconds) {
      case _ => Restart
    }

  private def generateIntervals(
    pullers: Seq[ActorRef],
    interval: Int
  ): Map[ActorRef, Int] =
    workers.map(_ -> interval).toMap

  private def prepareStatuses(
    pullers: Seq[ActorRef],
    statusMap: Map[ActorRef, String],
    intervalMap: Map[ActorRef, Int]
  ): Seq[PullerInfo] = {
    workers.map { worker =>
      PullerInfo(
        labels(worker),
        pullingAdresses(worker),
        intervalMap(worker),
        statusMap(worker)
      )
    }
  }

  private def createWorkers(): Seq[ActorRef] = {

    val networkInfoPuller = context.system.actorOf(
      Props(
        new MetricsPuller[NetworkInfo, NetworkInfoRepository](
          networkInfoRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      this.clusterAlias ++ "_network_info_puller"
    )
    networkInfoPuller ! SubscribeTransitionCallBack(self)

    val osInfoPuller =
      context.system.actorOf(
        Props(
          new MetricsPuller[OSInfo, OsInfoRepository](
            osInfoRepository,
            wSClient,
            dbConfigProvider
          )
        ),
        this.clusterAlias ++ "_os_info_puller"
      )
    osInfoPuller ! SubscribeTransitionCallBack(self)

    val runtimeInfoPuller = context.system.actorOf(
      Props(
        new MetricsPuller[RuntimeInfo, RuntimeInfoRepository](
          runtimeInfoRepository,
          wSClient,
          dbConfigProvider
        )
      ),
      this.clusterAlias ++ "_runtime_info_puller"
    )
    runtimeInfoPuller ! SubscribeTransitionCallBack(self)

    val threadInfoPuller = context.system
      .actorOf(
        Props(
          new MetricsPuller[ThreadInfo, ThreadInfoRepository](
            threadInfoRepository,
            wSClient,
            dbConfigProvider
          )
        ),
        this.clusterAlias ++ "_thread_info+puller"
      )
    threadInfoPuller ! SubscribeTransitionCallBack(self)

    val logPuller = context.system
      .actorOf(
        Props(
          new MetricsPuller[LogEvent, LogEventRepository](
            logEventRepository,
            wSClient,
            dbConfigProvider
          )
        ),
        this.clusterAlias ++ "_log_puller"
      )
    logPuller ! SubscribeTransitionCallBack(self)

    Seq(networkInfoPuller, osInfoPuller, runtimeInfoPuller, threadInfoPuller, logPuller)
  }
}
