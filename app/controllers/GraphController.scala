package controllers

import akka.actor.ActorSystem
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{ AbstractController, Action, AnyContent, ControllerComponents }
import services.GraphService

import scala.concurrent.ExecutionContext

@Singleton
class GraphController @Inject()(
  system: ActorSystem,
  cc: ControllerComponents,
  wSClient: WSClient,
  graphService: GraphService,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getGraph(): Action[AnyContent] = Action.async { _ =>
    graphService.getGraph.map(result => Ok(Json.toJson(Seq(result))))
  }

}
