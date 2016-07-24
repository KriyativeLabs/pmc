package controllers

import javax.inject.Inject

import helpers.enums.{SmsType, UserType}
import helpers.json.UserSerializer
import helpers.{EmailService, CommonUtil, ResponseHelper}
import models._
import play.api._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import security.{Authentication, IsAuthenticated, PermissionCheckAction}

import scala.util.Random

class UsersController @Inject()(implicit val messagesApi: MessagesApi, implicit val mail: MailerClient) extends Controller with UserSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)
  val email = new EmailService()

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => badRequest(errors.mkString),
      user => {
        val newUser = user.copy(companyId = request.user.companyId)
        Users.insert(newUser.copy(status = true)) match {
          case Left(e) => badRequest(e)
          case Right(id) => created(Some(newUser), s"Successfully Created New User:${newUser.name}")
        }
      }
    )
  }

  def login() = Action(parse.json) { implicit request =>
    request.body.validate[LoginCase].fold(
      errors => badRequest(errors.mkString),
      login => {
        Users.login(login) match {
          case Left(l) => unAuthorized("Authentication failed!")
          case Right(r) => {
            val token = Authentication.encryptAuthHeader(r.id.get, r.companyId, 90, UserType.withName(r.accountType))
            val company = Companies.findById(r.companyId)
            request.getQueryString("account_type") match {
              case Some(x) if (x.toLowerCase == "internet" && !company.get.isCableNetwork) || (x.toLowerCase != "internet" && company.get.isCableNetwork) => {
                ok(Json.obj("token" -> token,
                  "name" -> r.name,
                  "company" -> company.get.name,
                  "cId" -> company.get.id.get,
                  "bSMS" -> company.get.bulkSMS,
                  "balanceReminder" -> company.get.balanceReminders,
                  "type" -> r.accountType),
                  s"Successfully logged in!")
              }
              case _ => unAuthorized("Authentication failed!")
            }
          }
        }
      }
    )
  }

  def updatePassword() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[PasswordChange].fold(
      errors => badRequest(errors.mkString),
      passwordData => {
        val userId = request.user.userId
        Users.updatePassword(userId, passwordData.oldPassword, passwordData.newPassword) match {
          case Left(e) => validationError("Password not updated!", e)
          case Right(r) => if (r == 0) failed("Password not updated! Old password not matching") else ok(Some("Password Updated Successfully"), s"Successfully Updated Password!")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val userDao = Users.findById(id.toInt)
    if (userDao.isDefined) ok(Json.toJson(userDao), "User details") else notFound(s"User with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val userList = Users.getAll()
    ok(Json.toJson(userList), "List of users")
  }

  def update(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    implicit val loggedInUser = request.user
    request.body.validate[User].fold(
      errors => badRequest(errors.mkString),
      user => {
        if (!user.id.isDefined || (user.id.isDefined && id != user.id.get)) validationError(user, "Id provided in url and data are not equal")
        else {
          Users.update(user) match {
            case Left(e) => validationError(user, e)
            case Right(r) => ok(Some(user), s"Successfully Updated!")
          }
        }
      }
    )
  }

  def delete(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    implicit val loggedInUser = request.user
    Users.findById(id) match {
      case Some(u) if u.companyId == loggedInUser.companyId => {
        Users.update(u.copy(status = false)) match {
          case Left(e) => validationError(u, e)
          case Right(r) => ok(Some(u), s"Successfully Deleted User!")
        }
      }
      case _ => validationError("Not Authorized to delete users", "Not Authorized to delete users")
    }
  }

  def forgotPassword(userId: String) = Action { implicit request =>
    Users.findByUserId(userId) match {
      case Some(user) if user.contactNo > 0 && user.contactNo.toString.length == 10 => {

        val newPassword = Random.alphanumeric.take(7).mkString
        Users.resetPassword(user.id.get, newPassword) match {
          case Left(e) => {
            validationError("Password not updated!", e)
          }
          case Right(r) => {
            SmsGateway.sendSms(s"Successfully reset password. Please use: $newPassword to login.", Some(user.contactNo), SmsType.GENERAL)
            ok(None, s"Successfully Sent Message to Mobile No:XXXXXXX${user.contactNo.toString.substring(7, 10)}!")
          }
        }
      }
      case _ => badRequest("Cannot find account/mobile no")
    }
  }

}