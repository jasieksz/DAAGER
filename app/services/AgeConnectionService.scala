package services

import cats.implicits._
import javax.inject.Inject
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AgeConnectionService @Inject()(
  wsClient: WSClient
) {

  def getClusterId(address: String)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val updatedAddress = if (address.startsWith("http")) address else "http://" + address
    Try(wsClient.url(updatedAddress).get.map(response => if (response.status == 200) Some(response.body) else None)).toOption
      .sequence[Future, Option[String]]
      .map(_.flatten)
  }

}
