package model.domain

import play.api.libs.json.Json

case class NodeDetails(
  address: String,
  id: String,
  nodeType: String,
  services: List[String]
)

object NodeDetails {
  implicit val format = Json.format[NodeDetails]

  val default = NodeDetails("","","", List[String]())
}
