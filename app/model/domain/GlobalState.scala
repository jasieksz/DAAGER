package model.domain

import play.api.libs.json.Json

case class GlobalState(
  nodesCount: Int,
  baseAddress: String,
  status: String
)

object GlobalState {
  implicit val format = Json.format[GlobalState]
}
