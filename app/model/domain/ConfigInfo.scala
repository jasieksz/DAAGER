package model.domain

import play.api.libs.json.Json

case class ConfigInfo(
  osInterval: Int,
  runtimeInterval: Int,
  threadInterval: Int,
  tcpInterval: Int
 )

object ConfigInfo {
  implicit val format = Json.format[ConfigInfo]
}