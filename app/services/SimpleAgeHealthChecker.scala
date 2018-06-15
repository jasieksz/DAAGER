package services

import akka.actor.ActorSystem
import cats.implicits._
import com.google.inject.Inject
import model.domain.NodeHealth
import org.joda.time.DateTime
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.ws.{ WSClient, WSResponse }
import repositories.NodeHealthRepository
import slick.jdbc.PostgresProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.util.Try

class SimpleAgeHealthChecker @Inject()(
  wsClient: WSClient,
  nodeHealthRepository: NodeHealthRepository,
  system: ActorSystem,
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[PostgresProfile] {

  private val hazelcastPrefix = "Hazelcast::"

  def run(address: String, interval: FiniteDuration): Unit = {
    val updatedAddress = if (address.startsWith("http")) address else "http://" + address
    system.scheduler.schedule(5 seconds, interval){
      Await.result(pullData(updatedAddress), Duration.Inf)
    }
  }

  private def pullData(ageAddress: String): Future[Unit] = {
    for {
      response: Option[WSResponse] <- Try(wsClient.url(ageAddress).get()).toOption.sequence
      nodeHealth = response.flatMap(getNodeHealth(_, ageAddress))
      _ = println(nodeHealth)
      _ <- nodeHealth.traverse[Future, Unit](value => db.run(nodeHealthRepository.save(value)))
    } yield ()
  }

  private def getNodeHealth(response: WSResponse, ageAddress: String): Option[NodeHealth] = {
    Try {
      val Array(
        nodeState,
        clusterState,
        clusterSafe,
        migrationQueueSize,
        clusterSize
      ) = response.body.split("\n")
        .map(_.stripPrefix(hazelcastPrefix).dropWhile(_ != '=').drop(1))
      NodeHealth(
        nodeState,
        clusterState,
        clusterSafe == "TRUE",
        migrationQueueSize.toInt,
        clusterSize.toInt,
        DateTime.now,
        ageAddress
      )
    }.toOption
  }

}
