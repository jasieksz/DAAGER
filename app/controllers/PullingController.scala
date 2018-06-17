package controllers

import javax.inject.{ Inject, Singleton }
import play.api.libs.json.{ JsError, Json, Reads }
import play.api.mvc._
import play.filters.csrf.AddCSRFToken
import services.{ AgeConnectionService, SimpleAgeHealthChecker }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import PullingRequest._

case class PullingRequest (value: String)
object PullingRequest {
  implicit val format = Json.format[PullingRequest]
}

@Singleton
class PullingController @Inject()(
  cc: ControllerComponents,
  ageConnectionService: AgeConnectionService,
  simpleAgeHealthChecker: SimpleAgeHealthChecker
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private def validateJson[A : Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def verify(): Action[PullingRequest] = Action(validateJson[PullingRequest]).async  { request =>
    val address = request.body.value
    ageConnectionService.isReachable(address).map( reachable =>
      if (reachable) Ok("") else BadRequest
    )
  }

  def startPulling(): Action[PullingRequest] = Action(validateJson[PullingRequest]) { request =>
    val address = request.body.value
    simpleAgeHealthChecker.run(address, 10 seconds)
    Ok("")
  }

  @AddCSRFToken
  def hello(): Action[AnyContent] = Action { _ => Ok("")}

}
