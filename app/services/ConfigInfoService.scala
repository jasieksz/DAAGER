package services

import javax.inject.Inject
import model.domain.ConfigInfo
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import repositories.ClustersRepository
import utils.DaagerPostgresProfile

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class ConfigInfoService @Inject()(
  wsClient: WSClient,
  clustersRepository: ClustersRepository,
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[DaagerPostgresProfile] {

  private val intervals = new mutable.HashMap[String, Int]()
  var logger            = Logger(getClass)

  def sendInitialConfigInfo(address: String, intervalValue: Int)(implicit ec: ExecutionContext): Unit = {
    intervals.put(address + "_osInterval", intervalValue)
    intervals.put(address + "_runtimeInterval", intervalValue)
    intervals.put(address + "_threadInterval", intervalValue)
    intervals.put(address + "_tcpInterval", intervalValue)

    wsClient
      .url(address + "/configure")
      .post(Json.toJson(ConfigInfo(intervalValue, intervalValue, intervalValue, intervalValue)))
      .map { response =>
        logger.info(response.body)
      }
  }

  def sendUpdateConfigInfo(intervalValue: Int, address: String, clusterAlias: String)(
    implicit ec: ExecutionContext
  ): Unit = {
    val intervalName: String = address.split('/').last + "Interval"
    for {
      baseAddress <- db.run(clustersRepository.findExistingByAlias(clusterAlias)).map(_.baseAddress)
      _ = intervals.put(baseAddress + "_" + intervalName, intervalValue)
      _ <- wsClient
        .url(baseAddress + address.split("/").head + "/configure")
        .post(
          Json.toJson(
            ConfigInfo(
              intervals("osInterval"),
              intervals("runtimeInterval"),
              intervals("threadInterval"),
              intervals("tcpInterval")
            )
          )
        )
        .map { response =>
          logger.info(response.body)
        }
    } yield ()
  }
}
