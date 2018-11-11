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

  def getGraph(clusterAlias: String): Action[AnyContent] = Action.async { _ =>
    graphService.getGraph(clusterAlias).map(result => Ok(Json.toJson(Seq(result))))
  }

  def getGlobalState(clusterAlias: String): Action[AnyContent] = Action.async { _ =>
    nodesInfoService.getGlobalState(clusterAlias).map(res => Ok(Json.toJson(res)))
  }

}
