package model.domain.metrics

import org.joda.time.DateTime

trait Metric {

  def address: String

  def timestamp: DateTime

  def clusterId: String

}
