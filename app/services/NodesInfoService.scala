package services

import java.net.URL

import akka.actor.ActorSystem
import javax.inject.Inject
import model.domain.{GlobalState, NodeDetails}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.ws.WSClient
import repositories.ClustersRepository
import repositories.metrics.OsInfoRepository
import utils.DaagerPostgresProfile

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class NodesInfoService @Inject()(
  system: ActorSystem,
  osInfoRepository: OsInfoRepository,
  clustersRepository: ClustersRepository,
  WSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[DaagerPostgresProfile] {

  private val clusterInfoParameter  = "/cluster"
  private val clusterStateParameter = "/cluster/detail"

  def getGlobalState(clusterAlias: String)(implicit ec: ExecutionContext): Future[GlobalState] = {
    for {
      address   <- getBaseAddress(clusterAlias)
      addresses <- getNodesAddresses(address.toString)
    } yield {
      GlobalState(
        addresses.length,
        address.toString,
        if (addresses.nonEmpty) "Ok" else "Idle"
      )
    }
  }

  def getNodesDetails(clusterAlias: String)(implicit ec: ExecutionContext): Future[Seq[NodeDetails]] = {
    for {
      address      <- getBaseAddress(clusterAlias)
      nodesDetails <- getNodeDetails(address)
    } yield {
      nodesDetails
    }
  }

  private def getBaseAddress(clusterAlias: String)(implicit ec: ExecutionContext): Future[String] =
    db.run(clustersRepository.findExistingByAlias(clusterAlias)).map(_.baseAddress)

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
        .map(_.json.validate[Seq[NodeDetails]].getOrElse(Seq.empty))
    } else {
      Future.successful(Seq.empty)
    }
  }

}
