package services

import javax.inject.Inject
import model.domain.ConfigInfo
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class ConfigInfoService @Inject()(wsClient: WSClient) {

  private val intervals           = new mutable.HashMap[String, Int]()
  var logger                      = Logger(getClass)
  private var baseAddress: String = new String

  def sendInitialConfigInfo(address: String, intervalValue: Int)(implicit ec: ExecutionContext): Unit = {
    intervals.put("osInterval", intervalValue)
    intervals.put("runtimeInterval", intervalValue)
    intervals.put("threadInterval", intervalValue)
    intervals.put("tcpInterval", intervalValue)
    baseAddress = address

    wsClient
      .url(address + "/configure")
      .post(Json.toJson(ConfigInfo(intervalValue, intervalValue, intervalValue, intervalValue)))
      .map { response =>
        logger.info(response.body)
      }
  }

  def sendUpdateConfigInfo(intervalValue: Int, address: String)(implicit ec: ExecutionContext): Unit = {
    val intervalName: String = address.split('/').last + "Interval"
    intervals.put(intervalName, intervalValue)
    wsClient
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
  }
}
