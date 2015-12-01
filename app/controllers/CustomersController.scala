package controllers

import helpers.enums.UserType
import helpers.json.CustomerSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Connection, Customers}
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
    println(request)
    request.body.validate[CustomerCreate].fold(
      errors => BadRequest(errors.mkString),
      customer => {
        implicit val loggedInUser = request.user
        Customers.insert(customer) match {
          case Left(e) => BadRequest(e)
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
    val customerList = Customers.getAll()
    ok(Json.toJson(customerList), "List of customers")
  }

  def unpaidCustomers() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val customerList = Customers.getUnpaidCustomers(request.user.userType, request.user.userId)
    ok(Json.toJson(customerList), "List of customers")
  }

  def paidCustomers() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val customerList = Customers.getPaidCustomers(request.user.userType, request.user.userId)
    ok(Json.toJson(customerList), "List of customers")
  }

  def searchCustomers(search: String) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val customerList = Customers.searchCustomers(search)
    ok(Json.toJson(customerList), "List of customers")
  }

  def update(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[CustomerCreate].fold(
      errors => BadRequest(errors.mkString),
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

}