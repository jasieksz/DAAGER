package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import repositories.ClustersRepository
import services.{GraphService, NodesInfoService}
import utils.DaagerPostgresProfile

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
  clustersRepository: ClustersRepository,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends AbstractController(cc)
  with HasDatabaseConfigProvider[DaagerPostgresProfile] {

  def getGraph(clusterAlias: String): Action[AnyContent] = Action.async { _ =>
    for {
      clusterExists <- db.run(clustersRepository.findByAlias(clusterAlias).map(_.isDefined))
      graph         <- graphService.getGraph(clusterAlias)
    } yield {
      if (!clusterExists) NotFound else Ok(Json.toJson(Seq(graph)))
    }
  }

  def getGlobalState(clusterAlias: String): Action[AnyContent] = Action.async { _ =>
    for {
      clusterExists <- db.run(clustersRepository.findByAlias(clusterAlias).map(_.isDefined))
      state         <- nodesInfoService.getGlobalState(clusterAlias)
    } yield {
      if (!clusterExists) NotFound else Ok(Json.toJson(Seq(state)))
    }
  }

}
