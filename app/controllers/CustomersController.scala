package controllers

import helpers.enums.UserType
import helpers.json.CustomerSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Customers, Customer}
import play.api.libs.json._
import security.{IsAuthenticated, PermissionCheckAction}
import play.api._
import play.api.mvc._

object CustomersController extends Controller with CustomerSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Customer].fold(
      errors => BadRequest(errors.mkString),
      customer => {
        val newCustomer = if(request.user.userType == UserType.OWNER) customer.copy(companyId = request.user.companyId) else customer
        Customers.insert(newCustomer) match {
          case Left(e) =>  BadRequest(e)
          case Right(id) => created (Some (newCustomer), s"Created Customer with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val customer = if(request.user.userType == UserType.ADMIN) Customers.findById(id.toInt) else Customers.findById(id.toInt,Some(request.user.companyId))
    if (customer.isDefined) ok(Json.toJson(customer), "Customer details") else notFound(s"Customer with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val customerList = if(request.user.userType == UserType.ADMIN ) Customers.getAll() else Customers.getAll(Some(request.user.companyId))
    ok(Json.toJson(customerList), "List of customers")
  }

  def unpaidCustomers() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val customerList =Customers.getUnpaidCustomers(request.user.userType,request.user.userId, request.user.companyId)
    ok(Json.toJson(customerList), "List of customers")
  }

  def searchCustomers(search:String) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    println(request.user)
    val customerList =Customers.searchCustomers(request.user.userType,request.user.userId, request.user.companyId,search)
    ok(Json.toJson(customerList), "List of customers")
  }

  def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Customer].fold(
      errors => BadRequest(errors.mkString),
      customer => {
        if(!customer.id.isDefined || (customer.id.isDefined && id != customer.id.get)) validationError(customer,"Id provided in url and data are not equal")
        else {
          Customers.update(customer) match {
            case Left(e) => validationError(customer, e)
            case Right(r) => ok(Some(customer), s"Updated Customer with details" + customer)
          }
        }
      }
    )
  }

}