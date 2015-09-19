package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.JdbcJodaSupport._

case class Payment(id: Option[Int], customerId: Int, paidAmount: Int, discountedAmount: Int, paidOn: DateTime, remarks: Option[String], agentId: Int, companyId: Int)

object Payment {
  implicit val fmt = Json.format[Payment]
}

class PaymentsTable(tag: Tag) extends Table[Payment](tag, "payments") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def customerId = column[Int]("customer_id")

  def paidAmount = column[Int]("paid_amount")

  def discountedAmount = column[Int]("discounted_amount")

  def paidOn = column[DateTime]("paid_on")

  def remarks = column[Option[String]]("remarks")

  def agentId = column[Int]("agent_id")

  def companyId = column[Int]("company_id")

  def * = (id.?, customerId, paidAmount, discountedAmount, paidOn, remarks, agentId, companyId) <>((Payment.apply _).tupled, Payment.unapply _)
}

object Payments {
  private lazy val paymentsQuery = TableQuery[PaymentsTable]

  def insert(payment: Payment): Either[String, Int] = {
    val resultQuery = paymentsQuery returning paymentsQuery.map(_.id) += payment
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  /*
    def update(payment: Payment): Either[String, Int] = {
      val updateQuery = paymentsQuery.filter(x => x.id === payment.id && x.companyId === payment.companyId).
        map(p => (p.name, p.city)).
        update(payment.name, payment.city)
      try {
        Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
      } catch {
        case e: Exception => Left(e.getMessage)
      }
    }
  */

  def findById(id: Int, companyId: Option[Int] = None): Option[Payment] = {
    val filterQuery = if (companyId.isDefined) paymentsQuery.filter(x => x.id === id && x.companyId === companyId.get) else paymentsQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Payment]]
  }

  def getAll(companyId: Option[Int] = None): Vector[Payment] = {
    val filterQuery = if (companyId.isDefined) paymentsQuery.filter(x => x.companyId === companyId.get) else paymentsQuery
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Payment]]
  }

  def findByDate(date: DateTime, companyId: Option[Int] = None): Option[Payment] = {
    val filterQuery = if (companyId.isDefined) paymentsQuery.filter(x => x.paidOn === date && x.companyId === companyId.get) else paymentsQuery.filter(x => x.paidOn === date)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Payment]]
  }

}