package models

import helpers.enums.SmsType
import org.joda.time.DateTime
import play.api.Play
import play.api.libs.json.Json
import security.LoggedInUser
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.JdbcJodaSupport._
import utils.{APIException, CommonUtils, EntityNotFoundException}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class Payment(id: Option[Int], receiptNo: String, customerId: Int, paidAmount: Int, discountedAmount: Int, paidOn: DateTime, remarks: Option[String], agentId: Int, companyId: Int)

object Payment {
  implicit val fmt = Json.format[Payment]
}

case class Credit(id: Option[Int], customerId: Int, connectionId: Option[Int], amount: Int, creditedOn: DateTime, generatedOn: DateTime, companyId: Int)

object Credit {
  implicit val fmt = Json.format[Credit]
}

case class PaymentCapsule(receiptNo: String, customerDetails: String, paidAmount: Int, paidOn: DateTime, remarks: Option[String], agentDetails: String)

object PaymentCapsule {
  implicit val fmt = Json.format[PaymentCapsule]
}

class PaymentsTable(tag: Tag) extends Table[Payment](tag, "payments") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def receiptNo = column[String]("receipt_no")

  def customerId = column[Int]("customer_id")

  def paidAmount = column[Int]("paid_amount")

  def discountedAmount = column[Int]("discounted_amount")

  def paidOn = column[DateTime]("paid_on")

  def remarks = column[Option[String]]("remarks")

  def agentId = column[Int]("agent_id")

  def companyId = column[Int]("company_id")

  def * = (id.?, receiptNo, customerId, paidAmount, discountedAmount, paidOn, remarks, agentId, companyId) <>((Payment.apply _).tupled, Payment.unapply _)
}

class CreditsTable(tag: Tag) extends Table[Credit](tag, "credits") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def customerId = column[Int]("customer_id")

  def connectionId = column[Option[Int]]("connection_id")

  def amount = column[Int]("amount")

  def creditedOn = column[DateTime]("credited_on")

  def generatedOn = column[DateTime]("generated_on")

  def companyId = column[Int]("company_id")

  def * = (id.?, customerId, connectionId, amount, creditedOn, generatedOn, companyId) <>((Credit.apply _).tupled, Credit.unapply _)
}

object Payments {
  private lazy val paymentsQuery = TableQuery[PaymentsTable]
  private lazy val customersQuery = TableQuery[CustomersTable]
  private lazy val agentsQuery = TableQuery[UsersTable]

  private lazy val creditsQuery = TableQuery[CreditsTable]

  val paymentCableSMSTemplate = Play.current.configuration.getString("sms.cable.payment.template").getOrElse(throw new APIException("SMS Settings not found in conf"))
  val paymentInternetSMSTemplate = Play.current.configuration.getString("sms.internet.payment.template").getOrElse(throw new APIException("SMS Settings not found in conf"))

  def insert(payment: Payment)(implicit loggedInUser: LoggedInUser): Either[String, Int] = {
    val customer = Customers.findById(payment.customerId).getOrElse(throw EntityNotFoundException(s"Customer not found with id:${payment.id}"))
    val company = Companies.findById(loggedInUser.companyId).getOrElse(throw EntityNotFoundException(s"Company not found with id:${payment.id}"))
    val receiptNo = company.id.get + "/" + DateTime.now().toString("YYYYMMdd") + "/" + Companies.nextReceiptNo(loggedInUser.companyId)

    val resultQuery = for {
      id <- paymentsQuery returning paymentsQuery.map(_.id) += payment.copy(receiptNo = receiptNo)
      customerUpdate <- customersQuery.filter(_.id === payment.customerId).map(_.balanceAmount).update((customer.balanceAmount - payment.paidAmount - payment.discountedAmount))
    } yield (id, customerUpdate)

    try {
      val result = DatabaseSession.run(resultQuery.transactionally).asInstanceOf[(Int, Int)]
      if (result._1 > 0 && result._2 == 1) {
        Notifications.createNotification(s"Payment collected from Customer(${customer.name}) ", loggedInUser.userId)
        if (company.smsEnabled) {
          val sms = (if (company.isCableNetwork) paymentCableSMSTemplate else paymentInternetSMSTemplate).
            replace("%%NAME%%", customer.name).
            replace("%%RECEIPT%%", receiptNo).
            replace("%%PAMOUNT%%", payment.paidAmount.toString).
            replace("%%BALANCE%%", (customer.balanceAmount - payment.paidAmount - payment.discountedAmount).toString)
          SmsGateway.sendSms(sms, customer.mobileNo, company, SmsType.PAYMENT_SMS)
        }
        //Await.result(future, Duration(5, SECONDS))
        Right(result._1)
      } else {
        Notifications.createNotification(s"Payment failed from Customer(${customer.name}) ", loggedInUser.userId)
        val resultQuery = for {
          id <- paymentsQuery.filter(_.id === result._1).delete
          customerUpdate <- customersQuery.filter(_.id === payment.customerId).map(_.balanceAmount).update(customer.balanceAmount)
        } yield (id, customerUpdate)
        DatabaseSession.run(resultQuery.transactionally).asInstanceOf[(Int, Int)]
        Left("Payment failed due to some error! Please try again")
      }
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findCreditsByCustomerId(customerId: Int): Vector[Credit] = {
    val query = creditsQuery.filter(_.customerId === customerId).sortBy(_.id.desc).drop(0).take(120)
    DatabaseSession.run(query.result).asInstanceOf[Vector[Credit]]
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

  def findById(id: Int)(implicit loggedInUser: LoggedInUser): Option[Payment] = {
    val filterQuery = paymentsQuery.filter(x => x.id === id && x.companyId === loggedInUser.companyId)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Payment]]
  }

  def getAll(pageNo:Int = 1, pageSize:Int = 20)(implicit loggedInUser: LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer), agent) <- (paymentsQuery.filter(x => x.companyId === loggedInUser.companyId).sortBy(_.id.desc).drop((pageNo - 1) * pageSize).take(pageSize) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, CustomerCore, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}"))
  }

  def findByCustId(id: Int)(implicit loggedInUser: LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer), agent) <- (paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.customerId === id)).sortBy(_.id.desc).take(10) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, CustomerCore, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}"))
  }

  def findByAgentId(id: Int)(implicit loggedInUser: LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer), agent) <- (paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.agentId === id && x.paidOn < CommonUtils.yesterday)).sortBy(_.id.desc).take(30) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, CustomerCore, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}"))
  }

  def findByAgentIdToday(id: Int)(implicit loggedInUser: LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer), agent) <- (paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.agentId === id && x.paidOn > CommonUtils.yesterday)).sortBy(_.paidOn.desc).take(30) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, CustomerCore, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

  def findByDate(date: DateTime)(implicit loggedInUser: LoggedInUser): Option[Payment] = {
    val filterQuery = paymentsQuery.filter(x => x.paidOn === date && x.companyId === loggedInUser.companyId)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Payment]]
  }

  def search(searchString: String)(implicit loggedInUser: LoggedInUser): Vector[PaymentCapsule] = {
    val date = CommonUtils.string2Date(searchString)
    val filterQuery = if (date.isDefined) {
      if (searchString.forall(_.isDigit)) {
        for {
          ((payment, customer), agent) <- (paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.paidAmount >= searchString.toInt) || (x.customerId === searchString.toInt) || (x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%") || (x.paidOn > date.get))).sortBy(_.id.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
        } yield (payment, customer, agent)
      } else {
        for {
          ((payment, customer), agent) <- (paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%") || (x.paidOn > date.get))).sortBy(_.id.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
        } yield (payment, customer, agent)
      }
    } else {
      if (searchString.forall(_.isDigit)) {
        for {
          ((payment, customer), agent) <- (paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.paidAmount >= searchString.toInt) || (x.customerId === searchString.toInt) || (x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%"))).sortBy(_.id.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
        } yield (payment, customer, agent)
      } else {
        for {
          ((payment, customer), agent) <- (paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%"))).sortBy(_.id.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
        } yield (payment, customer, agent)
      }
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, CustomerCore, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}"))
  }

  def searchByDateRange(startDate: DateTime, endDate: DateTime)(implicit loggedInUser: LoggedInUser): Vector[PaymentCapsule] = {
    val sDate = startDate.minusDays(1).withTime(23, 59, 59, 999)
    val eDate = endDate.withTime(23, 59, 59, 999)
    val filterQuery = for {
      ((payment, customer), agent) <- (paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.paidOn > sDate && x.paidOn < eDate)).sortBy(_.paidOn.desc) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)).sortBy(_._1._1.id.desc)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, CustomerCore, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}"))
  }


}