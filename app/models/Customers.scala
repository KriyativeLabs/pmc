package models

import helpers.enums.{ConnectionStatus, SmsType}
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Json
import security.LoggedInUser
import slick.driver.PostgresDriver.api._
import utils.EntityNotFoundException

import scala.concurrent.ExecutionContext.Implicits.global

case class CustomerCore(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String], address: String, companyId: Int, areaId: Int, houseNo: Option[String], balanceAmount: Int, createdBy: Option[Int], updatedBy: Option[Int])

object CustomerCore {
  implicit val fmt = Json.format[CustomerCore]
}

case class CustomerCapsule(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String], address: String, companyId: Int, areaId: Int, houseNo: Option[String], balanceAmount: Int, createdBy: Option[Int], updatedBy: Option[Int], connections: List[Connection]) {
  def this(core: CustomerCore, connections: List[Connection]) {
    this(core.id, core.name, core.mobileNo, core.emailId, core.address, core.companyId, core.areaId, core.houseNo, core.balanceAmount, core.createdBy, core.updatedBy, connections)
  }

  def core = CustomerCore(id, name, mobileNo, emailId, address, companyId, areaId, houseNo, balanceAmount, createdBy, updatedBy)
}

object CustomerCapsule {
  implicit val fmt = Json.format[CustomerCapsule]
}

class CustomersTable(tag: Tag) extends Table[CustomerCore](tag, "customers") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def mobileNo = column[Option[Long]]("mobile_no")

  def emailId = column[Option[String]]("email_id")

  def address = column[String]("address")

  def companyId = column[Int]("company_id")

  def areaId = column[Int]("area_id")

  def houseNo = column[Option[String]]("house_no")

  def balanceAmount = column[Int]("balance_amount")

  def createdBy = column[Option[Int]]("created_by")

  def updatedBy = column[Option[Int]]("updated_by")

  def * = (id.?, name, mobileNo, emailId, address, companyId, areaId, houseNo, balanceAmount, createdBy, updatedBy) <>((CustomerCore.apply _).tupled, CustomerCore.unapply _)
}

object Customers {

  val logger = Logger(this.getClass)

  private lazy val customerQuery = TableQuery[CustomersTable]
  private lazy val connectionsQuery = TableQuery[ConnectionsTable].filter(!_.isArchived.getOrElse(false))
  private lazy val iConnectionsQuery = TableQuery[ConnectionsTable]
  private lazy val creditsQuery = TableQuery[CreditsTable]

  def insert(customer: CustomerCapsule)(implicit loggedInUser: LoggedInUser): Either[String, Int] = {
    val newCustomer = CustomerCore(None, customer.name, customer.mobileNo, customer.emailId, customer.address, loggedInUser.companyId, customer.areaId, None, customer.balanceAmount, Some(loggedInUser.userId), Some(loggedInUser.userId))
    val houseNo = Areas.getIdSequence(newCustomer.areaId, newCustomer.companyId)
    houseNo match {
      case Left(e) => {
        Left(e)
      }
      case Right(s) => {
        try {
          val resultQuery = for {
            id <- customerQuery returning customerQuery.map(_.id) += newCustomer.copy(houseNo = Some(s))
            conns <- iConnectionsQuery ++= customer.connections.map(x => x.copy(customerId = Some(id), companyId = Some(loggedInUser.companyId)))
          } yield id
          val result = DatabaseSession.run(resultQuery).asInstanceOf[Int]
          Notifications.createNotification(s"New Customer(${customer.name}) with id(${result}) was added", loggedInUser.userId)
          val company = Companies.findById(loggedInUser.companyId).getOrElse(throw EntityNotFoundException(s"Company with id:${loggedInUser.companyId} not found"))
          if (company.isCableNetwork) {
            SmsGateway.sendSms(s"You have been registered for sms bill payments for cable operator:${company.name}", customer.mobileNo, company, SmsType.SUBSCRIPTION_SMS)
          } else {
            SmsGateway.sendSms(s"You have been registered for sms bill payments for Internet operator:${company.name}", customer.mobileNo, company, SmsType.SUBSCRIPTION_SMS)
          }
          Right(result)
        } catch {
          case e: Exception => Left(e.getMessage)
        }
      }
    }
  }

  def tempInsert(customer: CustomerCapsule, companyId: Int): Either[String, Int] = {
    val newCustomer = CustomerCore(None, customer.name, customer.mobileNo, customer.emailId, customer.address, companyId, customer.areaId, None, customer.balanceAmount, None, None)
    val houseNo = Areas.getIdSequence(newCustomer.areaId, newCustomer.companyId)
    houseNo match {
      case Left(e) => {
        Left(e)
      }
      case Right(s) => {
        try {
          val resultQuery = for {
            id <- customerQuery returning customerQuery.map(_.id) += newCustomer.copy(houseNo = Some(s))
            conns <- iConnectionsQuery ++= customer.connections.map(x => x.copy(customerId = Some(id), companyId = Some(companyId)))
          } yield id
          val result = DatabaseSession.run(resultQuery.transactionally).asInstanceOf[Int]
          Right(result)
        } catch {
          case e: Exception => e.printStackTrace();Left(e.getMessage)
        }
      }
    }
  }

  def updateAmount(customerId: Int, connectionId: Int, paidAmount: Int): Boolean = {
    val custQuery = customerQuery.filter(x => x.id === customerId)
    val custResult = DatabaseSession.run(custQuery.result.headOption).asInstanceOf[Option[CustomerCore]]
    val customer = custResult.getOrElse(throw EntityNotFoundException(s"Customer not found with Id:$customerId"))
    val now = DateTime.now().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfDay(0)
    val updateQuery = for {
      cUp <- customerQuery.filter(x => x.id === customerId && x.balanceAmount === customer.balanceAmount).map(c => c.balanceAmount).update(customer.balanceAmount - paidAmount)
      _ <- creditsQuery += Credit(None, customerId, Some(connectionId), paidAmount, now, DateTime.now(), customer.companyId)
    } yield cUp
    try {
      if (DatabaseSession.run(updateQuery.transactionally).asInstanceOf[Int] == 1) {
        true
      } else {
        false
      }
    } catch {
      case e: Throwable => {
        logger.error(s"Already Update connection($connectionId) for billing of customer $customerId")
        false
      }
    }
  }

  def update(customer: CustomerCapsule)(implicit loggedInUser: LoggedInUser): Either[String, Int] = {
    val conIdList = customer.connections.flatMap(_.id)
    val updateQuery = for {
      cust <- customerQuery.filter(x => x.id === customer.id && x.companyId === loggedInUser.companyId).map(p => (p.name, p.mobileNo, p.emailId, p.address, p.areaId, p.balanceAmount, p.updatedBy)).update(customer.name, customer.mobileNo, customer.emailId, customer.address, customer.areaId, customer.balanceAmount, Some(loggedInUser.userId))
      _ <- iConnectionsQuery.filter(con => con.customerId === customer.id && !(con.id inSet conIdList)).map(_.isArchived).update(Some(true))
      _ <- iConnectionsQuery ++= customer.connections.filter(_.id.isEmpty).map(_.copy(customerId = customer.id, msoStatus = Some(ConnectionStatus.UNKNOWN), companyId = Some(loggedInUser.companyId), isArchived = Some(false)))
    } yield cust
    try {
      customer.connections.filter(_.id.isDefined).foreach({ con =>
        logger.info("Updating Connection" + con)
        Connections.update(con.copy(isArchived = Some(false), companyId = Some(loggedInUser.companyId)))
      })
      Right(DatabaseSession.run(updateQuery.transactionally).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  /*
  def getUnpaidCustomers(userType: UserType, userId: Int, sortBy: Option[String], sortOrder: Option[String], pageSize: Option[Int], pageNo: Option[Int])(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    val filterQuery = if (pageSize.isDefined && pageNo.isDefined) {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount > 0) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc).drop((pageNo.get - 1) * pageSize.get).take(pageSize.get)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount > 0) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    }
    processResult(DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]])
  }
  */

  private def processResult(result: Vector[(CustomerCore, Option[Connection])]): Vector[CustomerCapsule] = {
    result.groupBy(_._1).map(c => new CustomerCapsule(c._1, c._2.flatMap(_._2).toList)).toVector.sortBy(_.balanceAmount).reverse
  }

  private def processResult_1(result: Vector[(CustomerCore, Connection)]): Vector[CustomerCapsule] = {
    result.groupBy(_._1).map(c => new CustomerCapsule(c._1, c._2.map(_._2).toList)).toVector.sortBy(_.balanceAmount).reverse
  }

  /*def getPaidCustomers(userType: UserType, userId: Int, sortBy: Option[String], sortOrder: Option[String], pageSize: Option[Int], pageNo: Option[Int])(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    val filterQuery = if (pageSize.isDefined && pageNo.isDefined) {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount === 0) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc).drop((pageNo.get - 1) * pageSize.get).take(pageSize.get)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId && x.balanceAmount === 0) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    }
    processResult(DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]])
  }


  def searchCustomers(search: String)(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
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

    processResult(DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]]).++(processResult_1(DatabaseSession.run(filterQueryForConn.result).asInstanceOf[Vector[(Customer, Connection)]]))
  }*/

  def findById(id: Int)(implicit loggedInUser: LoggedInUser): Option[CustomerCapsule] = {
    val filterQuery = for {
      (cust, conn) <- customerQuery.filter(x => x.id === id && x.companyId === loggedInUser.companyId) joinLeft connectionsQuery on (_.id === _.customerId)
    } yield (cust, conn)
    processResult(DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(CustomerCore, Option[Connection])]]).headOption
  }

  def getAlll(sortBy: Option[String], sortOrder: Option[String], pageSize: Option[Int], pageNo: Option[Int])(implicit loggedInUser: LoggedInUser): Vector[CustomerCapsule] = {
    val filterQuery = if (pageSize.isDefined && pageNo.isDefined) {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId).sortBy(_.balanceAmount.desc).drop((pageNo.get - 1) * pageSize.get).take(pageSize.get) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    } else {
      for {
        (cust, conn) <- (customerQuery.filter(x => x.companyId === loggedInUser.companyId) joinLeft connectionsQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
      } yield (cust, conn)
    }
    processResult(DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(CustomerCore, Option[Connection])]])
  }

  def getAllCount()(implicit loggedInUser: LoggedInUser): Int = {
    val filterQuery = customerQuery.filter(x => x.companyId === loggedInUser.companyId).length
    DatabaseSession.run(filterQuery.result).asInstanceOf[Int]
  }

  def getAll(companyId: Int): Vector[(CustomerCore, Connection)] = {
    val filterQuery = for {
      (cust, conn) <- (customerQuery.filter(x => x.companyId === companyId).sortBy(_.id.asc) join connectionsQuery.filter(y => y.status === "ACTIVE") on (_.id === _.customerId)).sortBy(_._1.id.asc)
    } yield (cust, conn)

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(CustomerCore, Connection)]]
  }

  def getAll(companyId: Int, customerIdSeq: Int): Vector[(CustomerCore, Connection)] = {
    val filterQuery = for {
      (cust, conn) <- (customerQuery.filter(x => x.companyId === companyId && x.id > customerIdSeq).sortBy(_.id.asc) join connectionsQuery.filter(y => y.status === "ACTIVE") on (_.id === _.customerId)).sortBy(_._1.id)
    } yield (cust, conn)

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(CustomerCore, Connection)]]
  }

  def getAllWithFilters(companyId: Int, active: Option[Boolean], isPaid: Option[Boolean], search: Option[String], sortBy: Option[String] = None, sortOrder: Option[String] = None, pageSize: Option[Int] = None, pageNo: Option[Int] = None): Vector[CustomerCapsule] = {

    var filteredQuery = isPaid match {
      case Some(true) => customerQuery.filter(_.balanceAmount === 0)
      case Some(false) => customerQuery.filter(_.balanceAmount > 0)
      case None => customerQuery
    }

    val conQuery = active match {
      case Some(true) => connectionsQuery.filter(_.status.toLowerCase === "active")
      case Some(false) => connectionsQuery.filter(_.status.toLowerCase =!= "active")
      case None => connectionsQuery
    }

    var conResult: Vector[Int] = Vector()
    filteredQuery = search match {
      case Some(s) if s.length > 0 => {
        val filterQueryForConn = for {
          (conn, cust) <- conQuery.filter(y => (y.boxSerialNo.toLowerCase like s"%${s.toLowerCase}%") || (y.cafId.toLowerCase like s"%${s.toLowerCase}%") || (y.setupBoxId.toLowerCase like s"%${s.toLowerCase}%")) join filteredQuery.filter(x => x.companyId === companyId) on (_.customerId === _.id)
        } yield cust.id
        conResult = DatabaseSession.run(filterQueryForConn.result).asInstanceOf[Vector[Int]]

        if (s.forall(_.isDigit)) {
          val num = s.toLong
          if (num.toInt > 0) {
            filteredQuery.filter(x => x.balanceAmount >= num.toInt || x.mobileNo === num || (x.id inSet conResult))
          } else {
            filteredQuery.filter(x => x.mobileNo === num || (x.id inSet conResult))
          }
        } else {
          filteredQuery.filter(x => (x.emailId.toLowerCase like s"%${s.toLowerCase}%") || (x.name.toLowerCase like s"%${s.toLowerCase}%") || (x.houseNo.toLowerCase like s"%${s.toLowerCase}%") || (x.id inSet conResult))
        }
      }
      case _ => filteredQuery
    }

    if (active.isDefined) {
      val pageQuery = if (pageSize.isDefined && pageNo.isDefined) {
        for {
          (cust, conn) <- (filteredQuery.filter(x => x.companyId === companyId).sortBy(_.balanceAmount.desc).drop((pageNo.get - 1) * pageSize.get).take(pageSize.get) join conQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
        } yield (cust, conn)
      } else {
        for {
          (cust, conn) <- (filteredQuery.filter(x => x.companyId === companyId) join conQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
        } yield (cust, conn)
      }
      processResult_1(DatabaseSession.run(pageQuery.result).asInstanceOf[Vector[(CustomerCore, Connection)]])
    } else {
      val pageQuery = if (pageSize.isDefined && pageNo.isDefined) {
        for {
          (cust, conn) <- (filteredQuery.filter(x => x.companyId === companyId).sortBy(_.balanceAmount.desc).drop((pageNo.get - 1) * pageSize.get).take(pageSize.get) joinLeft conQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
        } yield (cust, conn)
      } else {
        for {
          (cust, conn) <- (filteredQuery.filter(x => x.companyId === companyId) joinLeft conQuery on (_.id === _.customerId)).sortBy(_._1.balanceAmount.desc)
        } yield (cust, conn)
      }
      processResult(DatabaseSession.run(pageQuery.result).asInstanceOf[Vector[(CustomerCore, Option[Connection])]])
    }
  }


}