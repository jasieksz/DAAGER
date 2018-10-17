package controllers

import akka.actor.ActorSystem
import controllers.GraphController.NodeDetailsRequest
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsError, Json, Reads}
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.{GraphService, NodesInfoService}

import scala.concurrent.ExecutionContext

object GraphController {
  case class NodeDetailsRequest(address: String)
  object NodeDetailsRequest {
    implicit val format = Json.format[NodeDetailsRequest]
  }
}

@Singleton
class GraphController @Inject()(
  system: ActorSystem,
  cc: ControllerComponents,
  wSClient: WSClient,
  graphService: GraphService,
  nodesInfoService: NodesInfoService,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def getGraph(): Action[AnyContent] = Action.async { _ =>
    graphService.getGraph.map(result => Ok(Json.toJson(Seq(result))))
  }

  def getGlobalState(): Action[AnyContent] = Action.async { _ =>
    nodesInfoService.getGlobalState.map(res => Ok(Json.toJson(res)))
  }

  def getNodeDetails(): Action[NodeDetailsRequest] =
    Action(validateJson[NodeDetailsRequest]).async { request =>
      nodesInfoService
        .getNodeDetails(request.body.address)
        .map(res => Ok(Json.toJson(res)))
    }

  private def validateJson[A : Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

}
