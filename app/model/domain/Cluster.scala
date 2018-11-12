package model.domain
import play.api.libs.json.Json

case class Cluster(
  clusterId: String,
  alias: String,
  baseAddress: String,
  isActive: Boolean = false
)

object Cluster {
  implicit val format = Json.format[Cluster]
}
