package actors

import actors.NodesKeeper.{ CurrentClients, GetClients }
import akka.actor.Actor

object NodesKeeper {

  case class CurrentClients(adresses: Seq[String])

  case object GetClients

  val name = "nodes-keeper"

}

class NodesKeeper extends Actor {

  private val clients: Seq[String] = Seq.empty

  override def receive: Receive = onMessage(clients)

  private def onMessage(clients: Seq[String]): Receive = {
    case CurrentClients(adresses) =>
      context.become(onMessage(adresses))
    case GetClients =>
      sender ! clients
  }

}
