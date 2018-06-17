package controllers

import actors.MetricsSupervisor
import actors.MetricsSupervisor.Start
import play.api.mvc._
import akka.actor._
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{ JsError, Json, Reads }
import play.api.libs.ws.WSClient
import repositories.metrics.{ NetworkInfoRepository, OsInfoRepository, RuntimeInfoRepository, ThreadInfoRepository }

import scala.concurrent.ExecutionContext

case class StartRequest (address: String, interval: Int)
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
) (implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val supervisor = system.actorOf(Props(
    classOf[MetricsSupervisor],
    networkInfoRepository,
    osInfoRepository,
    runtimeInfoRepository,
    threadInfoRepository,
    wSClient,
    dbConfigProvider),
    "metrics-supervisor"
  )

  def startPulling(): Action[StartRequest] = Action(validateJson[StartRequest]) { request =>
    supervisor ! Start(request.body.address, request.body.interval)
    Ok("")
  }

  private def validateJson[A : Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

}
