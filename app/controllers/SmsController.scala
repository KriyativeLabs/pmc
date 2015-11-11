package controllers

import helpers.enums.UserType
import helpers.json.SmsSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Sms, SmsGateway}
import play.api._
import play.api.mvc._
import security.{IsAuthenticated, PermissionCheckAction}

object SmsController extends Controller with SmsSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def sendSms() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[Sms].fold(
      errors => BadRequest(errors.mkString),
      sms => {
        implicit val loggedInUser = request.user
        SmsGateway.send(sms) match {
          case Left(e) => failed(s"Failed to send sms!")
          case Right(msg) => ok(Some(msg), "Sent Sms Successfully")
        }
      }
    )
  }
}
