package controllers

import actors.MetricsSupervisor
import actors.MetricsSupervisor.Start
import play.api.mvc._
import akka.actor._
import akka.pattern.Patterns
import akka.util.Timeout
import javax.inject._
import model.domain.PullerInfo
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{ JsError, Json, Reads }
import play.api.libs.ws.WSClient
import repositories.metrics.{ NetworkInfoRepository, OsInfoRepository, RuntimeInfoRepository, ThreadInfoRepository }

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

case class StartRequest(baseAddress: String, interval: Int)

object StartRequest {
  implicit val format = Json.format[StartRequest]
}

@Singleton
class MetricsController @Inject()(
  system: ActorSystem,
  cc: ControllerComponents,
  networkInfoRepository: NetworkInfoRepository,
  osInfoRepository: OsInfoRepository,
  runtimeInfoRepository: RuntimeInfoRepository,
  threadInfoRepository: ThreadInfoRepository,
  wSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val timeout: Timeout = 4 seconds

  private val supervisor = system.actorOf(Props(
    classOf[MetricsSupervisor],
    networkInfoRepository,
    osInfoRepository,
    runtimeInfoRepository,
    threadInfoRepository,
    wSClient,
    dbConfigProvider),
    MetricsSupervisor.name
  )

  def startPulling(): Action[StartRequest] = Action(validateJson[StartRequest]) { request =>
    val address = request.body.baseAddress
    val updatedAddress = if (address.startsWith("http://")) address else "http://" + address
    supervisor ! Start(updatedAddress, request.body.interval)
    Ok("")
  }

  def getStatuses: Action[AnyContent] = Action.async {
    Patterns.ask(supervisor, MetricsSupervisor.GetStatus, timeout)
      .map { case res: Seq[PullerInfo] => Ok(Json.toJson(res)) }
  }

  def changeInterval(): Action[MetricsSupervisor.UpdateInterval] = Action(validateJson[MetricsSupervisor.UpdateInterval]) { request =>
    supervisor ! request.body
    Ok("")
  }

  def stopPuller(): Action[MetricsSupervisor.Stop] = Action(validateJson[MetricsSupervisor.Stop]) { request =>
    supervisor ! request.body
    Ok("")
  }

  private def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

}
