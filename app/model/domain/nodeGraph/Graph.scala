package model.domain.nodeGraph

import play.api.libs.json.Json

case class Graph(nodes: Seq[Node], edges: Seq[Edge])

object Graph {
  implicit val format = Json.format[Graph]
}
