package model.domain.nodeGraph

import play.api.libs.json.Json

case class Node(id: String, label: String)

object Node {
  implicit val format = Json.format[Node]
}
