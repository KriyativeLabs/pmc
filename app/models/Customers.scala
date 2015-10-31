package models

import controllers.CustomerCreate
import helpers.enums.UserType
import helpers.enums.UserType.UserType
import helpers.enums.UserType.UserType
import play.api.libs.json.Json
import security.{LoggedInUser_1, LoggedInUser}
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

//import scala.concurrent.ExecutionContext.Implicits.global

case class Customer(id: Option[Int], name: String, mobileNo: Long, emailId: String, address: String, companyId: Int, areaId: Int, houseNo: Option[String], balanceAmount: Int)

object Customer {
  implicit val fmt = Json.format[Customer]
}

case class CustomerCapsule(customer:Customer, connection:Option[Connection])

object CustomerCapsule {
  implicit val fmt = Json.format[CustomerCapsule]
}

class CustomersTable(tag: Tag) extends Table[Customer](tag, "customers") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def mobileNo = column[Long]("mobile_no")

  def emailId = column[String]("email_id")

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

  def insert(customer: CustomerCreate, companyId:Int): Either[String, Int] = {
    val newCustomer = Customer(None, customer.name, customer.mobileNo, customer.emailId, customer.address, companyId, customer.areaId, None, customer.balanceAmount)
    val houseNo = Areas.getIdSequence(newCustomer.areaId, newCustomer.companyId)
    houseNo match {
      case Left(e) => {
        Left(e)
      }
      case Right(s) => {
        try {
          val resultQuery = for {
            id <- customerQuery returning customerQuery.map(_.id) += newCustomer.copy(houseNo = Some(s))
            conns <- connectionsQuery ++= customer.connections.map(x => x.copy(customerId = Some(id),companyId= Some(companyId)))
          } yield id
          Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
        } catch {
          case e: Exception => Left(e.getMessage)
        }
      }
    }
  }

  def updateAmount(customerId: Int, companyId: Int, paidAmount: Int): Either[String, Int] = {
    val customer = findById(customerId, Some(companyId))
    if (customer.isDefined) {
      val updateQuery = customerQuery.filter(x => x.id === customerId && x.balanceAmount === customer.get.balanceAmount).
        map(c => c.balanceAmount).
        update(customer.get.balanceAmount - paidAmount)
      try {
        Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
      } catch {
        case e: Exception => Left(e.getMessage)
      }
    } else Left(s"Customer with id:$customerId not found")
  }

  def update(customer: Customer): Either[String, Int] = {
    val updateQuery = customerQuery.filter(x => x.id === customer.id && x.companyId === customer.companyId).
      map(p => (p.name, p.mobileNo, p.emailId, p.address, p.areaId, p.houseNo)).
      update(customer.name, customer.mobileNo, customer.emailId, customer.address, customer.areaId, customer.houseNo)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def getUnpaidCustomers(userType: UserType, userId: Int, companyId: Int): Vector[Customer] = {
    val filterQuery = if (userType == UserType.OWNER) {
      for {
        customers <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0)
      } yield customers
    } else {
      for {
        (customers, areaMap) <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0) join agentAreaQuery.filter(_.agentId === userId) on (_.areaId === _.areaId)
      } yield customers
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }

  def getPaidCustomers(userType: UserType, userId: Int, companyId: Int): Vector[Customer] = {
    println(LoggedInUser_1().userId)
    val filterQuery = if (userType == UserType.OWNER) {
      for {
        customers <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount === 0)
      } yield customers
    } else {
      for {
        (customers, areaMap) <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount === 0) join agentAreaQuery.filter(_.agentId === userId) on (_.areaId === _.areaId)
      } yield customers
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }

  def searchCustomers(userType: UserType, userId: Int, companyId: Int, search: String): Vector[Customer] = {
    val filterQuery = if (userType == UserType.OWNER) {
      if (search.forall(_.isDigit)) {
        val num = search.toLong
        for {
          customers <- customerQuery.filter(x => x.companyId === companyId && (x.balanceAmount >= num.toInt || x.mobileNo === num))
        } yield customers
      } else {
        for {
          customers <- customerQuery.filter(x => x.companyId === companyId && ((x.emailId.toLowerCase like s"%${search.toLowerCase}%") || (x.name.toLowerCase like s"%${search.toLowerCase}%") || (x.houseNo.toLowerCase like s"%${search.toLowerCase}%")))
        } yield customers
      }
    } else {
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
    }

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }


  def findById(id: Int, companyId: Option[Int] = None): Option[Customer] = {
    val filterQuery = if (companyId.isDefined) customerQuery.filter(x => x.id === id && x.companyId === companyId.get) else customerQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Customer]]
  }

  def getAll(companyId: Option[Int] = None): Vector[CustomerCapsule] = {

    val filterQuery = for {(cust, conn) <- (if (companyId.isDefined) customerQuery.filter(x => x.companyId === companyId.get).sortBy(_.balanceAmount) else customerQuery.sortBy(_.balanceAmount)) joinLeft connectionsQuery on (_.id === _.customerId)
    } yield (cust, conn)

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Customer, Option[Connection])]].map(x => (CustomerCapsule.apply _).tupled(x))
  }
}