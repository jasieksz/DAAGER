package utils
import com.github.tminglei.slickpg._
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

trait DaagerPostgresProfile extends ExPostgresProfile with PgArraySupport with PgDate2Support with PgDateSupportJoda {

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api = DaagerApi

  object DaagerApi extends API with ArrayImplicits with DateTimeImplicits {
    implicit val strListTypeMapper =
      new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

}

object DaagerPostgresProfile extends DaagerPostgresProfile
