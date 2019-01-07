package services
import model.domain.metrics.Metric
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Reads
import play.api.libs.ws.WSClient
import repositories.metrics.MetricsRepository
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import utils.instances.DbioInstances._
import cats.implicits._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class DataPullingService[M <: Metric, Repo <: MetricsRepository[M, _]](
  repository: Repo,
  wSClient: WSClient,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit reads: Reads[M])
  extends HasDatabaseConfigProvider[PostgresProfile] {

  private val log = Logger(getClass)

  def getData(address: String)(implicit ec: ExecutionContext): Future[Unit] = {
    val data = wSClient
      .url(address)
      .addHttpHeaders("Accept" -> "application/json")
      .withRequestTimeout(2 seconds)
      .get()
      .map(
        res =>
          res.json
            .validate[Seq[M]]
            .fold(
              err => {
                log.error(
                  s"Error in $address puller actor }" + err.toString + "\n" + res.body
                )
                Seq.empty
              },
              logResultInfo
          )
      )
    val dbAction = DBIO
      .from(data)
      .flatMap(values => values.toList.traverse(repository.save))
    db.run(dbAction).void
  }

  private def logResultInfo(res: Seq[M]): Seq[M] = {
    log.info(s"Successfully downloaded ${res.length} ${res.headOption
      .map(info => info.getClass.toString.stripPrefix("class model.domain.metrics."))
      .getOrElse("")} messages")
    res
  }

  def isReachable(address: String)(implicit ec: ExecutionContext): Future[Boolean] =
    wSClient.url(address).get().map(_.status == 200)

}
