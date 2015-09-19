package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import com.github.tototoshi.slick.JdbcJodaSupport._
import slick.driver.PostgresDriver.api._

case class Connection(id: Option[Int], customerId: Option[Int], setupBoxId: String, boxSerialNo:String, planId: Int, discount: Int, installationDate: DateTime,
                      status: String, cafId: String, idProof: String, companyId: Option[Int])

object Connection {
  implicit val fmt = Json.format[Connection]
}

class ConnectionsTable(tag: Tag) extends Table[Connection](tag, "connections") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def customerId = column[Option[Int]]("customer_id")

  def setupBoxId = column[String]("setup_box_id")

  def boxSerialNo = column[String]("box_serial_no")

  def planId = column[Int]("plan_id")

  def discount = column[Int]("discount")

  def installationDate = column[DateTime]("installation_date")

  def status = column[String]("status")

  def cafId = column[String]("caf_id")

  def idProof = column[String]("id_proof")

  def companyId = column[Option[Int]]("company_id")

  def * = (id.?, customerId, setupBoxId, boxSerialNo, planId, discount, installationDate, status, cafId, idProof, companyId) <>((Connection.apply _).tupled, Connection.unapply _)
}

object Connections {
  private lazy val connectionQuery = TableQuery[ConnectionsTable]

  def insert(connection: Connection): Either[String, Int] = {
    val resultQuery = connectionQuery returning connectionQuery.map(_.id) += connection
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(connection: Connection): Either[String, Int] = {
    val updateQuery = connectionQuery.filter(x => x.id === connection.id && x.companyId === connection.companyId).
      map(c => (c.setupBoxId, c.planId, c.discount, c.installationDate, c.status, c.cafId, c.idProof)).
      update(connection.setupBoxId, connection.planId, connection.discount, connection.installationDate, connection.status, connection.cafId, connection.idProof)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findById(id: Int, companyId: Option[Int] = None): Option[Connection] = {
    val filterQuery = if (companyId.isDefined) connectionQuery.filter(x => x.id === id && x.companyId === companyId.get) else connectionQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Connection]]
  }

  def getAll(companyId: Option[Int] = None): Vector[Connection] = {
    val filterQuery = if (companyId.isDefined) connectionQuery.filter(x => x.companyId === companyId.get) else connectionQuery
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Connection]]
  }
}