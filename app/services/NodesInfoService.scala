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
  private val timeout: Timeout      = 10 seconds
  private val clusterInfoParameter  = "/cluster"
  private val clusterStateParameter = "/cluster/detail"

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

  def getNodesDetails(implicit ec: ExecutionContext): Future[Seq[NodeDetails]] = {
    for {
      address      <- getBaseAddress
      nodesDetails <- getNodeDetails(address)
    } yield {
      nodesDetails
    }
  }

  private def getBaseAddress(implicit ec: ExecutionContext): Future[String] = {
    Patterns
      .ask(metricsSupervisor, MetricsSupervisor.GetConfig, timeout)
      .map(_.toString)
  }

  private def getNodesAddresses(baseAddress: String)(implicit ec: ExecutionContext): Future[Seq[String]] = {
    if (Try(new URL(baseAddress).toURI).isSuccess) {
      WSClient
        .url(baseAddress + clusterInfoParameter)
        .get()
        .map(_.json.validate[Seq[String]].getOrElse(Seq.empty))
    } else {
      Future.successful(Seq.empty)
    }
  }

  private def getNodeDetails(baseAddress: String)(implicit ec: ExecutionContext): Future[Seq[NodeDetails]] = {
    if (Try(new URL(baseAddress).toURI).isSuccess) {
      WSClient
        .url(baseAddress + clusterStateParameter)
        .addHttpHeaders("Accept" -> "application/json")
        .withRequestTimeout(2 seconds)
        .get()
        .map(asd => { println(asd); asd })
        .map(_.json.validate[Seq[NodeDetails]].getOrElse(Seq.empty))
    } else {
      Future.successful(Seq.empty)
    }
  }

}
