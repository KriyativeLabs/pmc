package controllers

import helpers.enums.UserType
import helpers.json.UserSerializer
import helpers.{CommonUtil, ResponseHelper}
import models._
import play.api.libs.json._
import security.{Authentication, IsAuthenticated, PermissionCheckAction}
import play.api._
import play.api.mvc._

object UsersController extends Controller with UserSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => BadRequest(errors.mkString),
      user => {
        val newUser = user.copy(companyId = request.user.companyId)
        Users.insert(newUser) match {
          case Left(e) =>  BadRequest(e)
          case Right(id) => created (Some (newUser), s"Successfully Created New User:${newUser.name}")
        }
      }
    )
  }

  def login() = Action(parse.json) { implicit request =>
    request.body.validate[LoginCase].fold(
      errors => BadRequest(errors.mkString),
      login => {
        Users.login(login) match {
          case Left(l) =>  unAuthorized("Authentication failed!")
          case Right(r) => {
            val token = Authentication.encryptAuthHeader(r.id.get, r.companyId, 3, UserType.withName(r.accountType))
            val company = Companies.findById(r.companyId)
            request.getQueryString("account_type") match {
              case Some(x) if x.toLowerCase == "internet" => {
                if(!company.get.isCableNetwork) {
                  ok(Json.obj("token" -> token, "name" -> r.name, "company" -> company.get.name, "type" -> r.accountType), s"Successfully logged in!")
                }
                else {
                  unAuthorized("Authentication failed!")
                }
              }
              case _ => {
                if(company.get.isCableNetwork) {
                  ok(Json.obj("token" -> token, "name" -> r.name, "company" -> company.get.name, "type" -> r.accountType), s"Successfully logged in!")
                }
                else {
                  unAuthorized("Authentication failed!")
                }
              }
            }
          }
        }
      }
    )
  }

  def updatePassword() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[PasswordChange].fold(
      errors => BadRequest(errors.mkString),
      passwordData => {
        val userId = request.user.userId
        Users.updatePassword(userId, passwordData.oldPassword, passwordData.newPassword) match {
          case Left(e) => validationError("Password is not updated!", e)
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

  def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    implicit val loggedInUser = request.user
    request.body.validate[User].fold(
      errors => BadRequest(errors.mkString),
      user => {
        if(!user.id.isDefined || (user.id.isDefined && id != user.id.get)) validationError(user,"Id provided in url and data are not equal")
        else {
          Users.update(user) match {
            case Left(e) => validationError(user, e)
            case Right(r) => ok(Some(user), s"Updated User with details" + user)
          }
        }
      }
    )
  }

}