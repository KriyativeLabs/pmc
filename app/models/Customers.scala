package models

import helpers.enums.UserType
import helpers.enums.UserType.UserType
import helpers.enums.UserType.UserType
import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class Customer(id:Option[Int],name:String,mobileNo:Long,emailId:String,address:String,companyId:Int,areaId:Int,houseNo:String,balanceAmount:Int)
object Customer {
  implicit val fmt = Json.format[Customer]
}

class CustomersTable(tag: Tag) extends Table[Customer](tag, "customers"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def mobileNo = column[Long]("mobile_no")
  def emailId = column[String]("email_id")
  def address = column[String]("address")
  def companyId = column[Int]("company_id")
  def areaId = column[Int]("area_id")
  def houseNo = column[String]("house_no")
  def balanceAmount = column[Int]("balance_amount")

  def * = (id.?,name,mobileNo,emailId,address,companyId,areaId,houseNo,balanceAmount) <> ((Customer.apply _).tupled, Customer.unapply _)
}

object Customers {
  private lazy val customerQuery = TableQuery[CustomersTable]
  private lazy val agentAreaQuery = TableQuery[AgentAreaMappingTable]

  def insert(customer: Customer): Either[String, Int] = {
    val resultQuery = customerQuery returning customerQuery.map(_.id) += customer
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def updateAmount(customerId:Int,companyId:Int,paidAmount:Int): Either[String, Int] = {
     val customer = findById(customerId,Some(companyId))
    if(customer.isDefined) {
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
      map(p => (p.name, p.mobileNo,p.emailId,p.address,p.areaId,p.houseNo)).
      update(customer.name, customer.mobileNo,customer.emailId,customer.address,customer.areaId,customer.houseNo)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def getUnpaidCustomers(userType:UserType,userId:Int, companyId:Int): Vector[Customer] = {
    val filterQuery = if(userType == UserType.OWNER){
      for{
        customers <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0)
      }yield customers
    } else {
      for {
        (customers, areaMap) <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0)join agentAreaQuery.filter(_.agentId === userId) on (_.areaId === _.areaId)
      } yield customers
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }

  def getPaidCustomers(userType:UserType,userId:Int, companyId:Int): Vector[Customer] = {
    val filterQuery = if(userType == UserType.OWNER){
      for{
        customers <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount === 0)
      }yield customers
    } else {
      for {
        (customers, areaMap) <- customerQuery.filter(x => x.companyId === companyId && x.balanceAmount === 0)join agentAreaQuery.filter(_.agentId === userId) on (_.areaId === _.areaId)
      } yield customers
    }
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }

  def searchCustomers(userType:UserType, userId:Int, companyId:Int, search:String): Vector[Customer] = {
    val filterQuery = if (userType == UserType.OWNER) {
      if (search.forall(_.isDigit)) {
        val num = search.toLong
        for {
          customers <- customerQuery.filter(x => x.companyId === companyId && (x.balanceAmount >= num.toInt || x.mobileNo === num))
        } yield customers
      } else {
        for {
          customers <- customerQuery.filter(x => x.companyId === companyId && ((x.emailId like s"%$search%") || (x.name like s"%$search%") || (x.houseNo like s"%$search%")))
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
          (areaMap, customers) <-  agentAreaQuery.filter(_.agentId === userId) join customerQuery.filter(x => x.companyId === companyId && ((x.emailId like s"%$search%") || (x.name like s"%$search%") || (x.houseNo like s"%$search%"))) on (_.areaId === _.areaId)
        } yield customers
      }
    }

    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }


  def findById(id: Int,companyId:Option[Int]=None): Option[Customer] = {
    val filterQuery = if(companyId.isDefined) customerQuery.filter(x => x.id === id && x.companyId === companyId.get) else customerQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Customer]]
  }

  def getAll(companyId:Option[Int]=None): Vector[Customer] = {
    val filterQuery = if(companyId.isDefined) customerQuery.filter(x => x.companyId === companyId.get) else customerQuery
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Customer]]
  }
}