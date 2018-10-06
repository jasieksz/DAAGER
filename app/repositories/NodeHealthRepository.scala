package repositories

import model.domain.NodeHealth
import model.infra.NodeHealths
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class NodeHealthRepository {

  private val query = NodeHealths.query

  def save(entity: NodeHealth)(implicit ec: ExecutionContext): DBIO[Unit] = {
    (query += entity) >> DBIO.successful(())
  }

  def findAll()(implicit ec: ExecutionContext): DBIO[Seq[NodeHealth]] = {
    query.result
  }

}
