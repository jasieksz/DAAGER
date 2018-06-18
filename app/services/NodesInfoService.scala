package services

import actors.{ MetricsSupervisor, NodesKeeper }
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.util.Timeout
import javax.inject.Inject
import model.domain.{ GlobalState, NodeDetails }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import repositories.metrics.OsInfoRepository
import slick.jdbc.PostgresProfile

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._

class NodesInfoService @Inject()(
  system: ActorSystem,
  osInfoRepository: OsInfoRepository,
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[PostgresProfile] {

  private val nodesKeeper = system.actorSelection("user/" + MetricsSupervisor.name + "/" + NodesKeeper.name)
  private val timeout: Timeout = 10 seconds

  def getGlobalState(implicit ec: ExecutionContext): Future[GlobalState] = {
    Patterns.ask(nodesKeeper, NodesKeeper.GetClients, timeout).map {
      case addresses: Seq[String] => GlobalState(
        addresses.length,
        addresses.headOption.map(_.stripSuffix("http://").takeWhile(_ != '/)).getOrElse("----"),
        "OK"
      )
    }
  }

  def getNodeDetails(nodeAddress: String)(implicit ec: ExecutionContext): Future[NodeDetails] = {
    db.run(osInfoRepository.findLastByAddress(nodeAddress)).map(osinfo =>
      NodeDetails(
        nodeAddress,
        osinfo.map(_.timestamp),
        osinfo.map(_.osSystemCpuLoad).getOrElse(0.0),
        osinfo.map(inf => inf.osFreePhysicalMemorySize.toDouble / inf.osTotalPhysicalMemorySize.toDouble).getOrElse(0.0)
      )
    )
  }

}
