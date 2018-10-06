package model.domain

import play.api.libs.json.Json

case class PullerInfo(
  label: String,
  address: String,
  interval: Int,
  status: String
)

object PullerInfo {
  implicit val format = Json.format[PullerInfo]
}