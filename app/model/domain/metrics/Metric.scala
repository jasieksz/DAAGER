package model.domain.metrics

import org.joda.time.DateTime

trait Metric {

  def address: String

  def date: DateTime

}
