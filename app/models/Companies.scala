package models

import helpers.enums.MSOType
import helpers.enums.MSOType.MSOType
import org.joda.time.DateTime
import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.JdbcJodaSupport._
import utils.{EnumUtils, EnumImplicits, APIException, EntityNotFoundException}

case class Company(id: Option[Int], name: String, owner: String, contactNo: Long, address: String, receiptNo: Long,
                   customerSeqNo: Option[Int], lastBillGeneratedOn: Option[DateTime], billStatus: Option[Boolean],
                   smsCount: Int, pricePerCustomer: Int, smsEnabled: Boolean, paymentSMS: Boolean, balanceReminders: Boolean,
                   bulkSMS: Boolean, subscriptionSMS: Boolean, isCableNetwork: Boolean, msoType: MSOType, cred1: Option[String],
                   cred2: Option[String], cred3: Option[String], cred4: Option[String])

object Company {
  implicit val msoFormat = EnumUtils.enumFormat(MSOType)
  implicit val fmt = Json.format[Company]
}

case class DashboardData(unpaidCustomers: Int, totalCustomers: Int, balanceAmount: Long, amountCollected: Int)

object DashboardData {
  implicit val fmt = Json.format[DashboardData]
}

class CompaniesTable(tag: Tag) extends Table[Company](tag, "companies") with EnumImplicits {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def owner = column[String]("company_owner")

  def contactNo = column[Long]("contact_no")

  def address = column[String]("address")

  def receiptNo = column[Long]("receipt_sequence")

  def customerSeqNo = column[Option[Int]]("customer_seq_no")

  def lastBillGeneratedOn = column[Option[DateTime]]("last_bill_generated_on")

  def billStatus = column[Option[Boolean]]("bill_status")

  def smsCount = column[Int]("sms_count")

  def pricePerCustomer = column[Int]("price_per_customer")

  def smsEnabled = column[Boolean]("sms_enabled")

  def paymentSMS = column[Boolean]("payment_sms")

  def balanceReminders = column[Boolean]("balance_reminders")

  def bulkSMS = column[Boolean]("bulk_sms")

  def subscriptionSMS = column[Boolean]("subscription_sms")

  def isCableNetwork = column[Boolean]("is_cable_network")

  def msoType = column[MSOType]("mso_type")

  def cred1 = column[Option[String]]("cred1")

  def cred2 = column[Option[String]]("cred2")

  def cred3 = column[Option[String]]("cred3")

  def cred4 = column[Option[String]]("cred4")

  def * = (id.?, name, owner, contactNo, address, receiptNo, customerSeqNo, lastBillGeneratedOn, billStatus, smsCount, pricePerCustomer, smsEnabled, paymentSMS, balanceReminders, bulkSMS, subscriptionSMS, isCableNetwork, msoType, cred1, cred2, cred3, cred4) <>((Company.apply _).tupled, Company.unapply _)
}

object Companies extends EnumImplicits {
  private lazy val companyQuery = TableQuery[CompaniesTable]
  private lazy val customerQuery = TableQuery[CustomersTable]
  private lazy val paymentsQuery = TableQuery[PaymentsTable]

  def insert(company: Company): Either[String, Int] = {
    val resultQuery = companyQuery returning companyQuery.map(_.id) += company.copy(receiptNo = 1)
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(company: Company): Either[String, Int] = {
    val updateQuery = companyQuery.filter(_.id === company.id).
      map(c => (c.name, c.owner, c.contactNo, c.address, c.smsEnabled, c.paymentSMS, c.balanceReminders, c.bulkSMS, c.subscriptionSMS)).
      update(company.name, company.owner, company.contactNo, company.address, company.smsEnabled, company.paymentSMS, company.balanceReminders, company.bulkSMS, company.subscriptionSMS)
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

  def dashboardData(companyId: Int): DashboardData = {
    val unpaidCustomerQuery = customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0)
    val unpaidCustomers = DatabaseSession.run(unpaidCustomerQuery.result).asInstanceOf[Vector[CustomerCore]]

    val totalCustomerQuery = customerQuery.filter(x => x.companyId === companyId).length
    val totalCustomersCount = DatabaseSession.run(totalCustomerQuery.result).asInstanceOf[Int]

    val lastMonthDate = DateTime.now().minusMonths(1).dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999)
    val amountCollectedQuery = paymentsQuery.filter(x => x.companyId === companyId && x.paidOn > lastMonthDate).map(_.paidAmount).sum
    val amountCollected = DatabaseSession.run(amountCollectedQuery.result).asInstanceOf[Option[Int]]

    DashboardData(unpaidCustomers.size, totalCustomersCount, unpaidCustomers.map(_.balanceAmount).sum, amountCollected.getOrElse(0))
  }

  def nextReceiptNo(companyId: Int): Long = {
    val company = findById(companyId).getOrElse(throw EntityNotFoundException(s"Company not found with Id:$companyId"))
    val query = companyQuery.filter(x => x.id === companyId && x.receiptNo === company.receiptNo).map(_.receiptNo).update(company.receiptNo + 1)
    if (DatabaseSession.run(query).asInstanceOf[Int] == 1) {
      company.receiptNo
    } else {
      throw new APIException("Cannot generate receipt right now. Please try again later!")
    }
  }

  def startBilling(companyId: Int): Boolean = {
    val updateQuery = companyQuery.filter(_.id === companyId).map(c => (c.billStatus, c.lastBillGeneratedOn, c.customerSeqNo)).update(Some(false), Some(DateTime.now().withDayOfMonth(1)), Some(0))
    DatabaseSession.run(updateQuery).asInstanceOf[Int] == 1
  }

  def endBilling(companyId: Int): Boolean = {
    val updateQuery = companyQuery.filter(_.id === companyId).map(_.billStatus).update(Some(true))
    DatabaseSession.run(updateQuery).asInstanceOf[Int] == 1
  }

  def updateCustomerSeqNo(companyId: Int, customerId: Int): Boolean = {
    val updateQuery = companyQuery.filter(_.id === companyId).map(_.customerSeqNo).update(Some(customerId))
    DatabaseSession.run(updateQuery).asInstanceOf[Int] == 1
  }

  def updateSMSCount(companyId: Int, count: Int = 1): Boolean = {
    val query = sqlu"update companies set sms_count = sms_count + #$count where id = #$companyId"
    DatabaseSession.run(query).asInstanceOf[Int] == 1
  }

}