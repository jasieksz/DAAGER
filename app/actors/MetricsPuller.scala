package actors

import actors.MetricsPuller._
import akka.actor.FSM
import cats.implicits._
import model.domain.metrics.Metric
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.WSClient
import repositories.metrics.MetricsRepository
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile

import utils.instances.DbioInstances._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object MetricsPuller {

  object Status extends Enumeration {
    type Status = Value
    val Idle, Testing, Pulling, Stopped, ErrorOccurred, Cancelled = Value
  }

  sealed trait Data

  case object Uninitialized extends Data

  final case class Stat(address: String, interval: Int, errors: Int) extends Data

  final case class StartPulling(address: String, interval: Int)

  final case class ChangeInterval(newInterval: Int)

  final case object Pull

  final case object Stop

  final case object GetStatus

}

class MetricsPuller[M <: Metric, Repo <: MetricsRepository[M, _]](
  repository: Repo,
  wSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit reads: Reads[M])
  extends FSM[Status.Value, Data]
  with HasDatabaseConfigProvider[PostgresProfile] {

  private case class ValuesList(list: Seq[M])(implicit reads: Reads[M])
  private implicit val listReads = Json.reads[ValuesList]

  import Status._

  implicit val ec = context.dispatcher

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(StartPulling(address, interval), Uninitialized) =>
      log.info("Received pulling request")
      val newState  = Stat(address, interval, 0)
      val reachable = Await.result(isReachable(address), 2 seconds)
      if (reachable) {
        log.info("Host reachable, starting pulling")
        pullAgainAfter(1 second)
        goto(Pulling) using newState
      } else {
        log.info(s"Host unreachable, tries ${1}")
        pullAgainAfter(5 seconds)
        goto(Testing) using newState.copy(errors = 1)
      }
    case Event(Pull, s: Stat) =>
      pullAgainAfter(1 second)
      goto(Pulling) using s
    case Event(ChangeInterval(newInterval), s: Stat) =>
      pullAgainAfter(1 second)
      goto(Pulling) using s.copy(interval = newInterval)
  }

  when(Testing) {
    case Event(Pull, Stat(add, interval, 10)) =>
      goto(Cancelled)
    case Event(Pull, st @ Stat(add, int, err)) =>
      val reachable = Await.result(isReachable(add), 2 seconds)
      if (reachable) {
        log.info("Host reachable, starting pulling")
        pullAgainAfter(1 seconds)
        goto(Pulling) using st.copy(errors = 0)
      } else {
        log.info(s"Host unreachable, tries ${err + 1}")
        pullAgainAfter(5 seconds)
        goto(Testing) using st.copy(errors = err + 1)
      }
  }

  when(Pulling) {
    case Event(Pull, s @ Stat(address, interval, err)) =>
      getData(address).onComplete {
        case Success(_) => pullAgainAfter(interval seconds)
        case Failure(e) =>
          log.error("Error occured during pulling")
          log.error(e.toString)
          goto(ErrorOccurred) using s.copy(errors = err + 1)
      }
      stay()
    case Event(Stop, s) =>
      goto(Idle) using s
    case Event(ChangeInterval(newInterval), s: Stat) =>
      stay using s.copy(interval = newInterval)
    case Event(StartPulling(address, interval), _) =>
      log.warning("Received start pulling request but already pulling")
      stay()
  }

  when(ErrorOccurred) {
    case _ =>
      log.error("error")
      stay()
  }

  when(Cancelled) {
    case _ =>
      log.error("cancelled")
      stay()
  }

  whenUnhandled {
    case _ =>
      log.error("Sum Ting Wong")
      stay()
  }

  private def pullAgainAfter(duration: FiniteDuration): Unit =
    context.system.scheduler.scheduleOnce(duration, self, Pull)

  private def getData(address: String)(implicit ec: ExecutionContext): Future[Unit] = {
    val data = wSClient
      .url(address)
      .addHttpHeaders("Accept" -> "application/json")
      .withRequestTimeout(2 seconds)
      .get()
      .map(
        res =>
          res.json
            .validate[Seq[M]]
            .fold(
              err => {
                log.error(
                  s"Error in $address puller actor }" + err.toString + "\n" + res.body
                )
                Seq.empty
              },
              logResultInfo
          )
      )
    val dbAction = DBIO
      .from(data)
      .flatMap(values => values.toList.traverse(repository.save))
    db.run(dbAction).void
  }

  private def logResultInfo(res: Seq[M]): Seq[M] = {
    log.info(s"Successfully downloaded ${res.length} ${res.headOption
      .map(info => info.getClass.toString.stripPrefix("class model.domain.metrics."))
      .getOrElse("")} messages")
    res
  }

  private def isReachable(address: String): Future[Boolean] =
    wSClient.url(address).get().map(_.status == 200)

}