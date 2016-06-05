package controllers

import helpers.enums.UserType
import helpers.json.CustomerSerializer
import helpers.{CommonUtil, ResponseHelper}
import models._
import org.joda.time.DateTime
import play.api._
import play.api.libs.json._
import play.api.mvc._
import security.{IsAuthenticated, PermissionCheckAction}

case class CustomerCreate(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String], address: String, areaId: Int, balanceAmount: Int, connections: List[Connection])

object CustomerCreate {
  implicit val fmt = Json.format[CustomerCreate]
}

object CustomersController extends Controller with CustomerSerializer with CommonUtil with ResponseHelper {

  val logger = Logger(this.getClass)


  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[CustomerCreate].fold(
      errors => badRequest(errors.mkString),
      customer => {
        implicit val loggedInUser = request.user
        Customers.insert(customer) match {
          case Left(e) => badRequest(e)
          case Right(id) => created(Some(customer), s"Successfully created new customer:${customer.name}")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val customer = Customers.findById(id.toInt)
    if (customer.isDefined) ok(Json.toJson(customer), "Customer details") else notFound(s"Customer with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val active = request.getQueryString("active") match {
      case Some(a) if a.toLowerCase == "true" =>  Some(true)
      case Some(a) if a.toLowerCase == "false" => Some(false)
      case _ => None
    }

    val isPaid = request.getQueryString("isPaid") match {
      case Some(a) if a.toLowerCase == "true" => Some(true)
      case Some(a) if a.toLowerCase == "false" => Some(false)
      case _ => None
    }

    val customerList = Customers.getAllWithFilters(loggedInUser.companyId, active, isPaid, request.getQueryString("q"), paginationAttributes._1,
      paginationAttributes._2, paginationAttributes._3, paginationAttributes._4)

    ok(Json.toJson(customerList), "List of customers")
  }

  def allCount() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val active = request.getQueryString("active") match {
      case Some(a) if a.toLowerCase == "true" =>  Some(true)
      case Some(a) if a.toLowerCase == "false" => Some(false)
      case _ => None
    }

    val isPaid = request.getQueryString("isPaid") match {
      case Some(a) if a.toLowerCase == "true" => Some(true)
      case Some(a) if a.toLowerCase == "false" => Some(false)
      case _ => None
    }

    val count = Customers.getAllWithFilters(loggedInUser.companyId, active, isPaid, request.getQueryString("q")).size

    ok(Json.obj("count" -> count), "List of customers count")
  }

/*
  def unpaidCustomers() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val pageAttributes = paginationAttributes
    val customerList = Customers.getUnpaidCustomers(request.user.userType, request.user.userId, pageAttributes._1, pageAttributes._2, pageAttributes._3, pageAttributes._4)
    ok(Json.toJson(customerList), "List of customers")
  }

  def paidCustomers() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val pageAttributes = paginationAttributes
    val customerList = Customers.getPaidCustomers(request.user.userType, request.user.userId, pageAttributes._1, pageAttributes._2, pageAttributes._3, pageAttributes._4)
    ok(Json.toJson(customerList), "List of customers")
  }

  def searchCustomers(search: String) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val customerList = Customers.searchCustomers(search)
    ok(Json.toJson(customerList), "List of customers")
  }
*/

  def update(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[CustomerCreate].fold(
      errors => badRequest(errors.mkString),
      customer => {
        implicit val loggedInUser = request.user
        if (!customer.id.isDefined || (customer.id.isDefined && id != customer.id.get)) validationError(customer, "Id provided in url and data are not equal")
        else {
          Customers.update(customer) match {
            case Left(e) => validationError(customer, e)
            case Right(r) => ok(Some(customer), s"Succesfully updated customer details for ${customer.name}")
          }
        }
      }
    )
  }

  def download() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    implicit val loggedInUser = request.user
    val HEADER = Companies.findById(loggedInUser.companyId) match {
      case Some(c) if c.isCableNetwork => "NAME,ADDRESS,STB NO,CAF/CAN NO,PLAN,COLLECTION/PAYMENT,STATUS,ACTIVATION/EXPIRY DATE"
      case Some(c) => "NAME,ADDRESS,USERNAME,IP,PLAN,COLLECTION/PAYMENT,STATUS,ACTIVATION/EXPIRY DATE"
      case None => "" //Not happens
    }
    var fileName = "customer_"
    val active = request.getQueryString("active") match {
      case Some(a) if a.toLowerCase == "true" => fileName += "active_"; Some(true)
      case Some(a) if a.toLowerCase == "false" => fileName += "inactive_"; Some(false)
      case _ => None
    }

    val isPaid = request.getQueryString("isPaid") match {
      case Some(a) if a.toLowerCase == "true" => fileName += "paid_";Some(true)
      case Some(a) if a.toLowerCase == "false" => fileName += "unpaid_";Some(false)
      case _ => None
    }

    val plans = Plans.getAll(Some(loggedInUser.companyId)).map(p => p.id.get -> s"${p.name}(${p.amount})").toMap

    fileName += "report.csv"
    ok(HEADER +"\n"+ Customers.getAllWithFilters(loggedInUser.companyId, active, isPaid, request.getQueryString("q")).map({ c =>
      c.connections.map({ con =>
        s"${c.customer.name},${c.customer.address}, ${con.setupBoxId},${con.cafId},${plans.getOrElse(con.planId, "No-Plan")}, ${c.customer.balanceAmount}, ${con.status.toUpperCase}, ${dateFormat(con.installationDate)}"
      }).mkString("\n")
    }).mkString("\n"), fileName, fileName)
  }

  private def dateFormat(date: Option[DateTime]): String = {
    date match {
      case Some(d) => d.toString("YYYY-MM-dd")
      case None => ""
    }
  }

  private def dateFormat(date: DateTime): String = {
    date.toString("YYYY-MM-dd")
  }
}