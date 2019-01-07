package actors

import actors.MetricsPuller.StartPulling
import actors.MetricsSupervisor._
import akka.actor.FSM.{CurrentState, Transition}
import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import model.domain.{PullerInfo, Worker}
import services.WorkersCreationService

import scala.concurrent.duration._
import scala.language.postfixOps

object MetricsSupervisor {

  final case class Start(baseAddress: String, interval: Int)

  final case class GetStatus(receiver: ActorRef)

  final case object GetConfig

  final case class UpdateInterval(workerAddress: String, newInterval: Int)

  final case class Stop(workerAddress: String)

  def props(clusterAlias: String, workersCreationService: WorkersCreationService) = Props(
    new MetricsSupervisor(clusterAlias, workersCreationService)
  )

}

class MetricsSupervisor(
  clusterAlias: String,
  workersCreationService: WorkersCreationService
) extends Actor {

  private val workers = workersCreationService.createWorkers(clusterAlias, context.system, self)

  private val intervals = Map.empty[Worker, Int].withDefault(_ => 0)

  private val actorStates = Map.empty[ActorRef, String].withDefault(_ => "Idle")

  override def receive: Receive = onMessage(actorStates, intervals, "----")

  private def onMessage(
    actorStates: Map[ActorRef, String],
    intervals: Map[Worker, Int],
    pullingAddress: String
  ): Receive = {
    case Start(baseAddress, interval) =>
      workers.foreach(
        worker =>
          worker.actor ! StartPulling(
            baseAddress + worker.address,
            interval
        )
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
      val worker = workers.find(_.address == address)
      worker.foreach(_.actor ! MetricsPuller.ChangeInterval(newInterval))
      context.become(
        onMessage(
          actorStates,
          intervals ++ worker.map(_ -> newInterval).toMap,
          pullingAddress
        )
      )
    case Stop(address) =>
      workers.find(_.address == address).foreach(_.actor ! MetricsPuller.Stop)
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 10 seconds) {
      case _ => Restart
    }

  override def postStop(): Unit =
    workers.map(_.actor).foreach(context stop)

  private def generateIntervals(
    pullers: Seq[Worker],
    interval: Int
  ): Map[Worker, Int] =
    pullers.map(_ -> interval).toMap

  private def prepareStatuses(
    pullers: Seq[Worker],
    statusMap: Map[ActorRef, String],
    intervalMap: Map[Worker, Int]
  ): Seq[PullerInfo] = {
    workers.map { worker =>
      PullerInfo(
        worker.label,
        worker.address,
        intervalMap(worker),
        statusMap(worker.actor)
      )
    }
  }

}
