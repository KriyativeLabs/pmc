package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.JdbcJodaSupport._


case class Company(id:Option[Int],name:String,owner:String,contactNo:Long,address:String)
object Company {
  implicit val fmt = Json.format[Company]
}

case class DashboardData(unpaidCustomers:Int, totalCustomers:Int, balanceAmount:Long, amountCollected:Long)
object DashboardData {
  implicit val fmt = Json.format[DashboardData]
}

class CompaniesTable(tag: Tag) extends Table[Company](tag, "companies"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def owner = column[String]("company_owner")
  def contactNo = column[Long]("contact_no")
  def address = column[String]("address")
  def * = (id.?,name,owner,contactNo,address) <> ((Company.apply _).tupled, Company.unapply _)
}

object Companies {
  private lazy val companyQuery = TableQuery[CompaniesTable]
  private lazy val customerQuery = TableQuery[CustomersTable]
  private lazy val paymentsQuery = TableQuery[PaymentsTable]

  def insert(company: Company): Either[String, Int] = {
    val resultQuery = companyQuery returning companyQuery.map(_.id) += company
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(company: Company): Either[String, Int] = {
    val updateQuery = companyQuery.filter(_.id === company.id).map(c => (c.name, c.owner, c.contactNo, c.address)).update(company.name, company.owner, company.contactNo, company.address)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findById(id: Int): Option[Company] = {
    val filterQuery = companyQuery.filter(_.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Company]]
  }

  def getAll: Vector[Company] = {
    DatabaseSession.run(companyQuery.result).asInstanceOf[Vector[Company]]
  }

  def dashboardData(companyId:Int): DashboardData={
    val unpaidCustomerQuery = customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0)
    val unpaidCustomers = DatabaseSession.run(unpaidCustomerQuery.result).asInstanceOf[Vector[Customer]]

    val totalCustomerQuery = customerQuery.filter(x => x.companyId === companyId).length
    val totalCustomersCount = DatabaseSession.run(totalCustomerQuery.result).asInstanceOf[Int]

    val lastMonthDate = DateTime.now().minusMonths(1).dayOfMonth().withMaximumValue().withTime(23,59,59,999)
    val amountCollectedQuery = paymentsQuery.filter(x => x.companyId === companyId && x.paidOn > lastMonthDate).map(_.paidAmount).sum
    val amountCollected = DatabaseSession.run(amountCollectedQuery.result).asInstanceOf[Option[Long]]

    DashboardData(unpaidCustomers.size,totalCustomersCount, unpaidCustomers.map(_.balanceAmount).sum, amountCollected.getOrElse(0))
  }

}