package model.domain.nodeGraph

import play.api.libs.json.Json

case class Edge(id: String, source: String, target: String)

object Edge {
  implicit val format = Json.format[Edge]
}
