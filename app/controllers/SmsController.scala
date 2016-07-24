package controllers

import javax.inject.Inject

import helpers.enums.{SmsType, UserType}
import helpers.json.SmsSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Sms, SmsGateway}
import play.api._
import play.api.i18n.MessagesApi
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import security.{IsAuthenticated, PermissionCheckAction}

class SmsController  @Inject()(implicit val messagesApi: MessagesApi, implicit val mail:MailerClient) extends Controller with SmsSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def sendSms() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[Sms].fold(
      errors => badRequest(errors.mkString),
      sms => {
        implicit val loggedInUser = request.user
        SmsGateway.send(sms, SmsType.BULK_SMS) match {
          case Left(e) => failed(s"Failed to send sms!")
          case Right(msg) => ok(Some(msg), "Sent Sms Successfully")
        }
      }
    )
  }
}
