package models

import controllers.CustomerCreate
import helpers.enums.UserType.UserType
import play.api.libs.json.Json
import security.LoggedInUser
import slick.driver.PostgresDriver.api._
import utils.EntityNotFoundException

import scala.concurrent.ExecutionContext.Implicits.global

//import scala.concurrent.ExecutionContext.Implicits.global

case class Customer(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String], address: String, companyId: Int, areaId: Int, houseNo: Option[String], balanceAmount: Int)

object Customer {
  implicit val fmt = Json.format[Customer]
}

case class CustomerCapsule(customer: Customer, connection: Option[Connection])

object CustomerCapsule {
  implicit val fmt = Json.format[CustomerCapsule]
}

class CustomersTable(tag: Tag) extends Table[Customer](tag, "customers") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def mobileNo = column[Option[Long]]("mobile_no")

  def emailId = column[Option[String]]("email_id")

  def address = column[String]("address")

  def companyId = column[Int]("company_id")

  def areaId = column[Int]("area_id")

  def houseNo = column[Option[String]]("house_no")

  def balanceAmount = column[Int]("balance_amount")

  def * = (id.?, name, mobileNo, emailId, address, companyId, areaId, houseNo, balanceAmount) <>((Customer.apply _).tupled, Customer.unapply _)
}

object Customers {
  private lazy val customerQuery = TableQuery[CustomersTable]
  private lazy val agentAreaQuery = TableQuery[AgentAreaMappingTable]
  private lazy val areasQuery = TableQuery[AreasTable]
  private lazy val connectionsQuery = TableQuery[ConnectionsTable]

  def insert(customer: CustomerCreate)(implicit loggedInUser: LoggedInUser): Either[String, Int] = {
    val newCustomer = Customer(None, customer.name, customer.mobileNo, customer.emailId, customer.address, loggedInUser.companyId, customer.areaId, None, customer.balanceAmount)
    val houseNo = Areas.getIdSequence(newCustomer.areaId, newCustomer.companyId)
    houseNo match {
      case Left(e) => {
        Left(e)
      }
      case Right(s) => {
        try {
          val resultQuery = for {
            id <- customerQuery returning customerQuery.map(_.id) += newCustomer.copy(houseNo = Some(s))
            conns <- connectionsQuery ++= customer.connections.map(x => x.copy(customerId = Some(id), companyId = Some(loggedInUser.companyId)))
          } yield id
          val result = DatabaseSession.run(resultQuery).asInstanceOf[Int]
          Notifications.createNotification(s"New Customer(${customer.name}) with id(${result}) was added", loggedInUser.userId)
          val company = Companies.findById(loggedInUser.companyId).getOrElse(throw EntityNotFoundException(s"Company with id:${loggedInUser.companyId} not found"))
          if (company.smsEnabled) {
            SmsGateway.sendSms(s"You have been registered for sms bill payments for cable operator:${company.name}", customer.mobileNo)
          }
          Right(result)
        } catch {
          case e: Exception => Left(e.getMessage)
        }
      }
    }
  }

  def tempinsert(customer: CustomerCreate): Either[String, Int] = {
    val newCustomer = Customer(None, customer.name, customer.mobileNo, customer.emailId, customer.address, 1, customer.areaId, None, customer.balanceAmount)
    val houseNo = Areas.getIdSequence(newCustomer.areaId, newCustomer.companyId)
    houseNo match {
      case Left(e) => {
        Left(e)
      }
      case Right(s) => {
        try {
          val resultQuery = for {
            id <- customerQuery returning customerQuery.map(_.id) += newCustomer.copy(houseNo = Some(s))
            conns <- connectionsQuery ++= customer.connections.map(x => x.copy(customerId = Some(id), companyId = Some(1)))
          } yield id
          val result = DatabaseSession.run(resultQuery).asInstanceOf[Int]
          //Notifications.createNotification(s"New Customer(${customer.name}) with id(${result}) was added", loggedInUser.userId)
          //val company = Companies.findById(loggedInUser.companyId).getOrElse(throw EntityNotFoundException(s"Company with id:${loggedInUser.companyId} not found"))
          //SmsGateway.sendSms(s"You have been registered for sms bill payments for cable operator:${company.name}", customer.mobileNo)
          Right(result)
        } catch {
          case e: Exception => Left(e.getMessage)
        }
      }
    }
  }

  def updateAmount(customerId: Int, paidAmount: Int): Boolean = {
    val custQuery = customerQuery.filter(x => x.id === customerId)
    val custResult = DatabaseSession.run(custQuery.result.headOption).asInstanceOf[Option[Customer]]
    val customer = custResult.getOrElse(throw EntityNotFoundException(s"Customer not found with Id:$customerId"))

    val updateQuery = customerQuery.filter(x => x.id === customerId && x.balanceAmount === customer.balanceAmount).
      map(c => c.balanceAmount).
      update(customer.balanceAmount - paidAmount)

    if (DatabaseSession.run(updateQuery).asInstanceOf[Int] == 1) {
      true
    } else {
      false
    }
  }


  def update(customer: CustomerCreate)(implicit loggedInUser: LoggedInUser): Either[String, Int] = {
    val con = customer.connections(0)
    val updateQuery = for {
      cust <- customerQuery.filter(x => x.id === customer.id && x.companyId === loggedInUser.companyId).map(p => (p.name, p.mobileNo, p.emailId, p.address, p.areaId, p.balanceAmount)).update(customer.name, customer.mobileNo, customer.emailId, customer.address, customer.areaId, customer.balanceAmount)
      conns <- connectionsQuery.filter(_.customerId === customer.id).map(c => (c.setupBoxId, c.boxSerialNo, c.cafId, c.discount, c.idProof, c.planId, c.status)).update(con.setupBoxId, con.boxSerialNo, con.cafId, con.discount, con.idProof, con.planId, con.status)
    } yield cust
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def getUnpaidCustomers(userType: UserType, userId: Int, sortBy: Option[String], sortOrder: Option[String], pageSize: Option[Int], pageNo: Option[Int])(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    //val filterQuery = if (userType == UserType.OWNER) {

    val filterQuery = if (pageSize.isDefined && pageNo.isDefined) {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount > 0) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc).drop((pageNo.get-1) * pageSize.get).take(pageSize.get)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount > 0)  joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    }



    /*} else {
      for {
        (customers, areaMap) <- customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount > 0) join agentAreaQuery.filter(_.agentId === userId) on (_.areaId === _.areaId)
      } yield customers
    }*/
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]].map(x => (CustomerCapsule.apply _).tupled(x))
  }

  def getPaidCustomers(userType: UserType, userId: Int, sortBy: Option[String], sortOrder: Option[String], pageSize: Option[Int], pageNo: Option[Int])(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    val filterQuery = if (pageSize.isDefined && pageNo.isDefined) {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount === 0) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc).drop((pageNo.get-1) * pageSize.get).take(pageSize.get)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount === 0)  joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]].map(x => (CustomerCapsule.apply _).tupled(x))
  }

  def searchCustomers(search: String)(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    //val filterQuery = if (userType == UserType.OWNER) {
    val filterQuery = if (search.forall(_.isDigit)) {
      val num = search.toLong
      for {
        (cust, conn) <- customerQuery.filter(x => x.companyId === loggedInUser.companyId && (x.balanceAmount >= num.toInt || x.mobileNo === num)) joinLeft connectionsQuery on (_.id === _.customerId)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- customerQuery.filter(x => x.companyId === loggedInUser.companyId && ((x.emailId.toLowerCase like s"%${search.toLowerCase}%") || (x.name.toLowerCase like s"%${search.toLowerCase}%") || (x.houseNo.toLowerCase like s"%${search.toLowerCase}%"))) joinLeft connectionsQuery on (_.id === _.customerId)
      } yield (cust, conn)
    }

    val filterQueryForConn = for {
      (conn, cust) <- connectionsQuery.filter(y => (y.boxSerialNo.toLowerCase like s"%${search.toLowerCase}%") || (y.cafId.toLowerCase like s"%${search.toLowerCase}%") || (y.setupBoxId.toLowerCase like s"%${search.toLowerCase}%")) join customerQuery.filter(x => x.companyId === loggedInUser.companyId) on (_.customerId === _.id)
    } yield (cust, conn)

    /*} else {
      if (search.forall(_.isDigit)) {
        val num = search.toLong
        for {
          (areaMap, customers) <- agentAreaQuery.filter(_.agentId === userId) join customerQuery.filter(x => x.companyId === companyId && (x.balanceAmount >= num.toInt || x.mobileNo === num)) on (_.areaId === _.areaId)
        } yield customers
      } else {
        for {
          (areaMap, customers) <- agentAreaQuery.filter(_.agentId === userId) join customerQuery.filter(x => x.companyId === companyId && ((x.emailId like s"%$search%") || (x.name like s"%$search%") || (x.houseNo like s"%$search%"))) on (_.areaId === _.areaId)
        } yield customers
      }
    }*/

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]].map(x => (CustomerCapsule.apply _).tupled(x)).++(DatabaseSession.run(filterQueryForConn.result).asInstanceOf[Vector[(Customer, Connection)]].map(x => CustomerCapsule(x._1, Some(x._2))))
  }


  def findById(id: Int)(implicit loggedInUser: LoggedInUser): Option[CustomerCapsule] = {
    val filterQuery = for {
      (cust, conn) <- customerQuery.filter(x => x.id === id && x.companyId === loggedInUser.companyId) joinLeft connectionsQuery on (_.id === _.customerId)
    } yield (cust, conn)

    val result = DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[(Customer, Option[Connection])]]
    if (result.isDefined) {
      Some((CustomerCapsule.apply _).tupled(result.get))
    } else {
      None
    }
  }

  def getAlll(sortBy: Option[String], sortOrder: Option[String], pageSize: Option[Int], pageNo: Option[Int])(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    val filterQuery = if (pageSize.isDefined && pageNo.isDefined) {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc).drop((pageNo.get-1) * pageSize.get).take(pageSize.get)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    }

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]].map(x => (CustomerCapsule.apply _).tupled(x))
  }

  def getAllCount()(implicit loggedInUser: LoggedInUser): Int = {
    val filterQuery = customerQuery.filter(x => x.companyId === loggedInUser.companyId).length
    DatabaseSession.run(filterQuery.result).asInstanceOf[Int]
  }

  def getAll(companyId: Int): Vector[CustomerCapsule] = {
    val filterQuery = for {
      (cust, conn) <- customerQuery.filter(x => x.companyId === companyId).sortBy(_.id.asc) join connectionsQuery.filter(y => y.status === "ACTIVE") on (_.id === _.customerId)
    } yield (cust, conn)

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Connection)]].map(x => CustomerCapsule(x._1, Some(x._2))) //(CustomerCapsule.apply _).tupled(x))
  }

  def getAll(companyId: Int, customerIdSeq: Int): Vector[CustomerCapsule] = {
    val filterQuery = for {
      (cust, conn) <- customerQuery.filter(x => x.companyId === companyId && x.id > customerIdSeq).sortBy(_.id.asc) join connectionsQuery.filter(y => y.status === "ACTIVE") on (_.id === _.customerId)
    } yield (cust, conn)

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Connection)]].map(x => CustomerCapsule(x._1, Some(x._2))) //(CustomerCapsule.apply _).tupled(x))
  }
}