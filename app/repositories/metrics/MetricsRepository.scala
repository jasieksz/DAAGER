package repositories.metrics

import slick.dbio.DBIO
import utils.DaagerPostgresProfile.api._

abstract class MetricsRepository[Entity, Tab <: Table[Entity]](val query: TableQuery[Tab]) {

  def save(entity: Entity): DBIO[Unit] =
    (query += entity) >> DBIO.successful(())

  def findAll(): DBIO[Seq[Entity]] =
    query.result

}
