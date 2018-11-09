package services

import java.net.URL

import actors.MetricsSupervisor
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.util.Timeout
import javax.inject.Inject
import model.domain.{GlobalState, NodeDetails}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.ws.WSClient
import repositories.metrics.OsInfoRepository
import utils.DaagerPostgresProfile

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class NodesInfoService @Inject()(
  system: ActorSystem,
  osInfoRepository: OsInfoRepository,
  WSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[DaagerPostgresProfile] {

  private val metricsSupervisor =
    system.actorSelection("user/" + MetricsSupervisor.name)
  private val timeout: Timeout     = 10 seconds
  private val clusterInfoParameter = "/cluster"

  def getGlobalState(implicit ec: ExecutionContext): Future[GlobalState] = {
    for {
      address   <- getBaseAddress
      addresses <- getNodesAddresses(address.toString)
    } yield {
      GlobalState(
        addresses.length,
        address.toString,
        if (addresses.nonEmpty) "Ok" else "Idle"
      )
    }
  }

  def getNodesAddresses(implicit ec: ExecutionContext): Future[Seq[String]] = {
    for {
      address   <- getBaseAddress
      addresses <- getNodesAddresses(address.toString)
    } yield addresses
  }

  private def getBaseAddress(implicit ec: ExecutionContext): Future[String] = {
    Patterns
      .ask(metricsSupervisor, MetricsSupervisor.GetConfig, timeout)
      .map(_.toString)
  }

  private def getNodesAddresses(
    baseAddress: String
  )(implicit ec: ExecutionContext): Future[Seq[String]] = {
    if (Try(new URL(baseAddress).toURI).isSuccess) {
      WSClient
        .url(baseAddress + clusterInfoParameter)
        .get()
        .map(_.json.validate[Seq[String]].getOrElse(Seq.empty))
    } else {
      Future.successful(Seq.empty)
    }
  }

  def getNodeDetails(
    nodeAddress: String
  )(implicit ec: ExecutionContext): Future[NodeDetails] = {
    db.run(osInfoRepository.findLastByAddress(nodeAddress))
      .map(osinfo => {
        NodeDetails(
          nodeAddress,
          osinfo.map(_.timestamp)
        )
      })
  }

}
