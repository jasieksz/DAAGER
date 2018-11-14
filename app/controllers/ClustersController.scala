package controllers

import actors.ClustersSupervisor
import actors.ClustersSupervisor.{AddCluster, Done, RemoveCluster}
import akka.actor._
import akka.pattern.Patterns
import akka.util.Timeout
import controllers.ClustersController.StartRequest
import javax.inject._
import model.domain.{Cluster, PullerInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._
import repositories.ClustersRepository
import services.{ConfigInfoService, MetricsSupervisorCreationService}
import utils.DaagerPostgresProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

object ClustersController {
  case class StartRequest(
    baseAddress: String,
    interval: Int,
    clusterId: String,
    alias: String
  )
  implicit val startRequestFormat = Json.format[StartRequest]
}

@Singleton
class ClustersController @Inject()(
  system: ActorSystem,
  cc: ControllerComponents,
  @Named("ClustersSupervisor")
  clustersSupervisor: ActorRef,
  metricsSupervisorCreationService: MetricsSupervisorCreationService,
  clustersRepository: ClustersRepository,
  configInfoService: ConfigInfoService,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends AbstractController(cc)
  with HasDatabaseConfigProvider[DaagerPostgresProfile] {

  private val timeout: Timeout = 4 seconds

  def getActiveClusters(): Action[AnyContent] = Action.async {
    db.run(clustersRepository.findActive()).map(res => Ok(Json.toJson(res)))
  }

  def startPulling(): Action[StartRequest] = Action(validateJson[StartRequest]).async { request =>
    val address        = request.body.baseAddress
    val updatedAddress = if (address.startsWith("http://")) address else "http://" + address
    val cluster        = Cluster(request.body.clusterId, request.body.alias, updatedAddress)
    for {
      clusterExists <- db.run(clustersRepository.exists(cluster.clusterId, cluster.alias))
      supervisorResponse <- if (!clusterExists)
        Patterns.ask(clustersSupervisor, AddCluster(cluster, request.body.interval), timeout)
      else Future.successful(())
      result <- handleSupervisorResponse(supervisorResponse, cluster, updatedAddress, request.body.interval)
    } yield result
  }

  def removeCluster(alias: String): Action[AnyContent] = Action {
    clustersSupervisor ! RemoveCluster(alias)
    Ok("")
  }

  def handleSupervisorResponse(response: Any, cluster: Cluster, updatedAddress: String, interval: Int): Future[Result] =
    response match {
      case Done =>
        configInfoService.sendInitialConfigInfo(updatedAddress, interval)
        db.run(clustersRepository.save(cluster.copy(isActive = true))).map(_ => Ok(""))
      case _ => Future.successful(BadRequest("Cluster exists"))
    }

  def getStatuses(clusterAlias: String): Action[AnyContent] = Action.async {
    Patterns
      .ask(clustersSupervisor, ClustersSupervisor.GetStatuses(clusterAlias), timeout)
      .map { case res: Seq[PullerInfo] => Ok(Json.toJson(res)) }
  }

  def changeInterval(): Action[ClustersSupervisor.UpdateInterval] =
    Action(validateJson[ClustersSupervisor.UpdateInterval]) { request =>
      configInfoService.sendUpdateConfigInfo(
        request.body.newInterval,
        request.body.workerAddress
      )
      Ok("")
    }

  def stopPuller(): Action[ClustersSupervisor.Stop] =
    Action(validateJson[ClustersSupervisor.Stop]) { request =>
      clustersSupervisor ! request.body
      Ok("")
    }

  private def validateJson[A : Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

}
