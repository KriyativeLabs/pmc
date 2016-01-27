package models

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

case class Payment(id: Option[Int], receiptNo:String, customerId: Int, paidAmount: Int, discountedAmount: Int, paidOn: DateTime, remarks: Option[String], agentId: Int, companyId: Int)

object Payment {
  implicit val fmt = Json.format[Payment]
}

case class PaymentCapsule(receiptNo:String, customerDetails:String, paidAmount: Int, paidOn: DateTime, remarks: Option[String], agentDetails:String)

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

object Payments {
  private lazy val paymentsQuery = TableQuery[PaymentsTable]
  private lazy val customersQuery = TableQuery[CustomersTable]
  private lazy val agentsQuery = TableQuery[UsersTable]
  val paymentSMSTemplate = Play.current.configuration.getString("sms.payment.template").getOrElse(throw new APIException("SMS Settings not found in conf"))

  def insert(payment: Payment)(implicit loggedInUser:LoggedInUser): Either[String, Int] = {
    val customer = Customers.findById(payment.customerId).getOrElse(throw EntityNotFoundException(s"Customer not found with id:${payment.id}"))
    val company = Companies.findById(loggedInUser.companyId).getOrElse(throw EntityNotFoundException(s"Company not found with id:${payment.id}"))
    val receiptNo = company.id.get + "/"+DateTime.now().toString("YYYYMMdd")+ "/" +Companies.nextReceiptNo(loggedInUser.companyId)

    val resultQuery = for {
      id <- paymentsQuery returning paymentsQuery.map(_.id) += payment.copy(receiptNo = receiptNo)
      customerUpdate <- customersQuery.filter(_.id === payment.customerId).map(_.balanceAmount).update((customer.customer.balanceAmount - payment.paidAmount-payment.discountedAmount))
    } yield id
    try {
      val result = DatabaseSession.run(resultQuery).asInstanceOf[Int]
      Notifications.createNotification(s"Payment collected from Customer(${customer.customer.name}) ", loggedInUser.userId)
      val sms = paymentSMSTemplate.
                replace("%%NAME%%",customer.customer.name).
                replace("%%RECEIPT%%",receiptNo).
                replace("%%PAMOUNT%%",payment.paidAmount.toString).
                replace("%%BALANCE%%",(customer.customer.balanceAmount - payment.paidAmount - payment.discountedAmount).toString)
      val future = Future {
        SmsGateway.sendSms(sms, customer.customer.mobileNo)
      }
      Await.result(future, Duration(5, SECONDS))
      Right(result)
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

  def findById(id: Int)(implicit loggedInUser:LoggedInUser): Option[Payment] = {
    val filterQuery = paymentsQuery.filter(x => x.id === id && x.companyId === loggedInUser.companyId)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Payment]]
  }

  def getAll()(implicit loggedInUser:LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer),agent) <- paymentsQuery.filter(x => x.companyId === loggedInUser.companyId).sortBy(_.paidOn.desc).take(200) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, Customer, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

  def findByCustId(id:Int)(implicit loggedInUser:LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer),agent) <- paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.customerId === id)).sortBy(_.paidOn.desc).take(10) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, Customer, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

  def findByAgentId(id:Int)(implicit loggedInUser:LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer),agent) <- paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.agentId === id && x.paidOn < CommonUtils.yesterday)).sortBy(_.paidOn.desc).take(30) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, Customer, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

  def findByAgentIdToday(id:Int)(implicit loggedInUser:LoggedInUser): Vector[PaymentCapsule] = {
    val filterQuery = for {
      ((payment, customer),agent) <- paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.agentId === id && x.paidOn > CommonUtils.yesterday)).sortBy(_.paidOn.desc).take(30) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, Customer, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

  def findByDate(date: DateTime)(implicit loggedInUser:LoggedInUser): Option[Payment] = {
    val filterQuery = paymentsQuery.filter(x => x.paidOn === date && x.companyId === loggedInUser.companyId)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Payment]]
  }

  def search(searchString:String)(implicit loggedInUser:LoggedInUser): Vector[PaymentCapsule] = {
    val date = CommonUtils.string2Date(searchString)
    val filterQuery =if(date.isDefined) {
      if(searchString.forall(_.isDigit)) {
        for {
          ((payment, customer), agent) <- paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.paidAmount >= searchString.toInt) || (x.customerId === searchString.toInt) || (x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%") || (x.paidOn > date.get))).sortBy(_.paidOn.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
        } yield (payment, customer, agent)
      } else {
        for {
          ((payment, customer), agent) <- paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%") || (x.paidOn > date.get))).sortBy(_.paidOn.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
        } yield (payment, customer, agent)
      }
    } else {
      if(searchString.forall(_.isDigit)) {
        for {
          ((payment, customer), agent) <- paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.paidAmount >= searchString.toInt) || (x.customerId === searchString.toInt) || (x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%"))).sortBy(_.paidOn.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
        } yield (payment, customer, agent)
      }else {
        for {
          ((payment, customer), agent) <- paymentsQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.receiptNo.toLowerCase like s"%${searchString.toLowerCase}%"))).sortBy(_.paidOn.desc).take(100) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
        } yield (payment, customer, agent)
      }
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, Customer, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

  def searchByDateRange(startDate:DateTime, endDate:DateTime)(implicit loggedInUser:LoggedInUser): Vector[PaymentCapsule] = {
    val sDate = startDate.minusDays(1).withTime(23, 59, 59,999)
    val eDate = endDate.withTime(23, 59, 59,999)
    val filterQuery = for {
      ((payment, customer),agent) <- paymentsQuery.filter(x => (x.companyId === loggedInUser.companyId && x.paidOn > sDate && x.paidOn < eDate )).sortBy(_.paidOn.desc) join customersQuery on (_.customerId === _.id) join agentsQuery on (_._1.agentId === _.id)
    } yield (payment, customer, agent)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, Customer, User)]].map(x => PaymentCapsule(x._1.receiptNo, s"${x._2.name}(${x._2.houseNo.get})", x._1.paidAmount, x._1.paidOn, x._1.remarks, s"${x._3.name}(${x._3.id.get})"))
  }

}