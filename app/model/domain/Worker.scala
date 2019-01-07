package model.domain
import akka.actor.ActorRef

case class Worker(
  actor: ActorRef,
  label: String,
  address: String
)
