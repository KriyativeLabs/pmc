package v1.models

import slick.jdbc.JdbcBackend.Database
import org.postgresql.util.PSQLException
import slick.dbio.{DBIOAction, NoStream}
import utils.{APIException, DbFkException, DbUniqueConstraintException}
import scala.concurrent.Await
import scala.concurrent.duration._

object DatabaseSession {
  val db = Database.forConfig("db")

  def apply() = db

  type QUERY = DBIOAction[Any, NoStream, Nothing]

  val timeout:Int = 20000

  def run(query: QUERY) = {
    try {
      Await.result(db.run(query), Duration(timeout, MILLISECONDS))
    } catch {
      case ex: PSQLException => {
        if (ex.getSQLState == "23505") {
          throw DbUniqueConstraintException(ex.getMessage)
        } else if (ex.getSQLState == "23503") {
          throw DbFkException(ex.getMessage)
        } else {
          throw new APIException(ex.getMessage)
        }
      }
    }
  }
}
