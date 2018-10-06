package services

import actors.{ MetricsSupervisor, NodesKeeper }
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.util.Timeout
import cats.implicits._
import javax.inject.Inject
import model.domain.nodeGraph.{ Edge, Graph, Node }

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class GraphService @Inject()(
  system: ActorSystem
) {

  private val nodesKeeper = system.actorSelection("user/" + MetricsSupervisor.name + "/" + NodesKeeper.name)
  private val timeout: Timeout = 10 seconds

  def getGraph()(implicit ec: ExecutionContext): Future[Graph] = {
    Patterns.ask(nodesKeeper, NodesKeeper.GetClients, timeout).map {
      case addresses: Seq[String] => createGraph(addresses)
    }
  }

  private def createGraph(addresses: Seq[String]): Graph = {
    val nodes = addresses.zipWithIndex.map { case (address, idx) =>
      Node(idx.toString, address)
    }.toList
    val edges = (nodes |@| nodes).tupled
      .zipWithIndex
      .map { case ((node1, node2), idx) => Edge(idx.toString, node1.id, node2.id) }
    Graph(nodes, edges)
  }

}
