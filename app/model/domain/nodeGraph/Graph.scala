package model.domain.nodeGraph

import model.domain.NodeDetails
import play.api.libs.json.Json

case class Graph(nodes: Seq[Node], edges: Seq[Edge], nodesDetails: Seq[NodeDetails])

object Graph {
  implicit val format = Json.format[Graph]
}
