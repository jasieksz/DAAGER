package controllers

import controllers.PullingRequest._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._
import play.filters.csrf.AddCSRFToken
import services.AgeConnectionService

import scala.concurrent.ExecutionContext

case class PullingRequest(value: String)

object PullingRequest {
  implicit val format = Json.format[PullingRequest]
}

@Singleton
class PullingController @Inject()(
  cc: ControllerComponents,
  ageConnectionService: AgeConnectionService,
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  private def validateJson[A : Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def verify(): Action[PullingRequest] =
    Action(validateJson[PullingRequest]).async { request =>
      val address = request.body.value
      Json.toJson(PullingRequest("asd"))
      ageConnectionService
        .getClusterId(address)
        .map(cluster_id => if (cluster_id.isDefined) Ok(cluster_id.get) else BadRequest)
    }

  @AddCSRFToken
  def hello(): Action[AnyContent] = Action { _ =>
    Ok("")
  }

}
