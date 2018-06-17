package actors

import actors.MetricsPuller._
import akka.actor.FSM
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.json.Reads
import play.api.libs.ws.WSClient
import repositories.metrics.MetricsRepository
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import cats.implicits._
import utils.instances.DbioInstances._

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

object MetricsPuller {

  object Status extends Enumeration {
    type Status = Value
    val Idle, Testing, Pulling, ErrorOccured, Cancelled = Value
  }

  sealed trait Data

  case object Uninitialized extends Data

  final case class State(address: String, interval: Int, errors: Int) extends Data

  final case class StartPulling(address: String, interval: Int)

  final case object Pull

  final case object GetStatus

}

class MetricsPuller[M, Repo <: MetricsRepository[M, _]](
  repository: Repo,
  wSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit reads: Reads[M]) extends FSM[Status.Value, Data] with HasDatabaseConfigProvider[PostgresProfile] {

  import Status._

  implicit val ec = context.dispatcher

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(StartPulling(address, interval), Uninitialized) =>
      log.info("Received pulling request")
      val newState = State(address, interval, 0)
      val reachable = Await.result(isReachable(address), 2 seconds)
      if (reachable) {
        log.info("Host reachable, starting pulling")
        pullAgainAfter(1 seconds)
        goto(Pulling) using newState
      } else {
        log.info(s"Host unreachable, tries ${1}")
        pullAgainAfter( 5 seconds)
        goto(Testing) using newState.copy(errors = 1)
      }
  }

  when(Testing) {
    case Event(Pull, State(add, interval, 10)) =>
      goto(Cancelled)
    case Event(Pull, st @ State(add, int, err)) =>
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
    case Event(Pull, s @ State(address, interval, err)) =>
      getData(address).onComplete {
        case Success(_) => pullAgainAfter(interval seconds)
        case Failure(e) =>
          log.error("Error occured during pulling")
          log.error(e.toString)
          goto(ErrorOccured) using s.copy(errors = err + 1)
      }
      stay()
  }

  when(ErrorOccured) {
    case _ =>
      log.error("error")
      stay()
  }

  when(Cancelled) {
    case _ =>
      log.error("cancelled")
      stay()
  }

  private def pullAgainAfter(duration: FiniteDuration): Unit ={
    context.system.scheduler.scheduleOnce(duration, self, Pull)
  }

  private def getData(address: String)(implicit ec: ExecutionContext): Future[Unit] = {
    val data = wSClient.url(address)
      .addHttpHeaders("Accept" -> "application/json")
      .withRequestTimeout(2 seconds)
      .get().map(res => res.json.validate[Seq[M]].get)
    val dbAction = DBIO.from(data).flatMap(values => values.toList.traverse(repository.save))
    db.run(dbAction).void
  }

  private def isReachable(adrress: String): Future[Boolean] = {
    wSClient.url(adrress).get().map(x => {println(x); x}).map(_.status == 200)
  }

}
