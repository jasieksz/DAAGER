package services

import cats.implicits._
import javax.inject.Inject
import model.domain.nodeGraph.{Edge, Graph, Node}

import scala.concurrent.{ExecutionContext, Future}

class GraphService @Inject()(nodesInfoService: NodesInfoService) {

  def getGraph()(implicit ec: ExecutionContext): Future[Graph] =
    nodesInfoService.getNodesAddresses.map(createGraph)

  private def createGraph(addresses: Seq[String]): Graph = {
    val nodes = addresses.zipWithIndex.map {
      case (address, idx) =>
        Node(idx.toString, address)
    }.toList
    val edges = (nodes |@| nodes).tupled.zipWithIndex
      .map {
        case ((node1, node2), idx) => Edge(idx.toString, node1.id, node2.id)
      }
    Graph(nodes, edges)
  }

}
