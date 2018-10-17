package services

import akka.util.Timeout
import javax.inject.Inject
import model.domain.ConfigInfo
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ConfigInfoService @Inject()(wsClient: WSClient) {

  private val intervals: mutable.HashMap[String, Int] = new mutable.HashMap[String, Int]()
  private val timeout: Timeout = 10 seconds
  var logger = Logger(getClass)

  def sendInitialConfigInfo(intervalValue: Int)(implicit ec: ExecutionContext): Unit = {
    intervals.put("osInterval", intervalValue)
    intervals.put("runtimeInterval", intervalValue)
    intervals.put("threadInterval", intervalValue)
    intervals.put("tcpInterval", intervalValue)

    wsClient.url("/config").post(Json.toJson(ConfigInfo(intervalValue, intervalValue, intervalValue, intervalValue)))
      .map { response =>
        val statusText: String = response.statusText
        logger.info(response.body)
      }
  }

  def sendUpdateConfigInfo(intervalValue: Int, intervalName: String)(implicit ec: ExecutionContext): Unit = {
    intervals.put(intervalName, intervalValue)
    wsClient.url("/config").post(
      Json.toJson(ConfigInfo(
        intervals("osInterval"),
        intervals("runtimeInterval"),
        intervals("threadInterval"),
        intervals("tcpInterval")
      ))).map { response =>
      val statusText: String = response.statusText
      logger.info(response.body)
    }
  }

}
