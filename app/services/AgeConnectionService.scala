package services

import cats.implicits._
import javax.inject.Inject
import play.api.libs.ws.WSClient

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

class AgeConnectionService @Inject()(
  wsClient: WSClient
) {

  def isReachable(address: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val updatedAddress = if (address.startsWith("http")) address else "http://" + address
    println(updatedAddress)
    Try(wsClient.url(updatedAddress).get.map(response => {println(response);response}).map(_.status == 200)) // TODO change to some kind of verification
      .toOption
      .sequence[Future, Boolean].map(_.getOrElse(false))
  }

}
