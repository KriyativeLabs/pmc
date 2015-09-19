package controllers

import helpers.enums.UserType
import helpers.json.PaymentSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Payments, Payment}
import play.api.libs.json._
import security.{IsAuthenticated, PermissionCheckAction}
import play.api._
import play.api.mvc._

object PaymentsController extends Controller with PaymentSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Payment].fold(
      errors => BadRequest(errors.mkString),
      payment => {
        val newPayment = if (request.user.userType != UserType.ADMIN) payment.copy(companyId = request.user.companyId) else payment
        Payments.insert(newPayment) match {
          case Left(e) => BadRequest(e)
          case Right(id) => created(Some(newPayment), s"Created Payment with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    val payment = if (request.user.userType == UserType.OWNER) Payments.findById(id.toInt, Some(request.user.companyId)) else Payments.findById(id.toInt)
    if (payment.isDefined) ok(Json.toJson(payment), "Payment details") else notFound(s"Payment with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val paymentList = if (request.user.userType != UserType.ADMIN) Payments.getAll(Some(request.user.companyId)) else Payments.getAll()
    ok(Json.toJson(paymentList), "List of payments")
  }

  /*
    def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
      request.body.validate[Payment].fold(
        errors => BadRequest(errors.mkString),
        payment => {
          if(!payment.id.isDefined || (payment.id.isDefined && id != payment.id.get)) validationError(payment,"Id provided in url and data are not equal")
          else {
            Payments.update(payment) match {
              case Left(e) => validationError(payment, e)
              case Right(r) => ok(Some(payment), s"Updated Payment with details" + payment)
            }
          }
        }
      )
    }
    */

}