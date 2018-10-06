package controllers

import play.api.mvc._
import akka.actor._
import javax.inject._
import model.domain.metrics.{ NetworkInfo, OSInfo, RuntimeInfo, ThreadInfo }
import org.joda.time.DateTime
import play.api.libs.json.Json

@Singleton
class MetricsMockController @Inject()(
  system: ActorSystem,
  cc: ControllerComponents
) extends AbstractController(cc) {

  private val mockAdress = "127.0.0.1:12345"

  def now: DateTime = DateTime.now()

  def getNetworkInfo(): Action[AnyContent] = Action { _ =>
    val info = NetworkInfo(now, mockAdress, 1, 1, 1)
    Ok(Json.toJson(Seq(info)))
  }

  def getThreadInfo(): Action[AnyContent] = Action { _ =>
    val info = ThreadInfo(now, mockAdress, 4, 4)
    Ok(Json.toJson(Seq(info)))
  }

  def getOSInfo(): Action[AnyContent] = Action { _ =>
    val info = OSInfo(now, mockAdress, 0.8, 0.8, 0.8, 123, 123, 1231, 123)
    Ok(Json.toJson(Seq(info)))
  }

  def getRuntimeInfo(): Action[AnyContent] = Action { _ =>
    val info = RuntimeInfo(now, mockAdress, 4, 23123123, 123123123, 123123123, 123123123)
    Ok(Json.toJson(Seq(info)))
  }

}