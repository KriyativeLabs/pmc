package controllers

import javax.inject.Inject

import helpers.enums.UserType
import helpers.json.ConnectionSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Connection, Connections}
import play.api._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import security.{IsAuthenticated, PermissionCheckAction}

class ConnectionsController @Inject()(implicit val messagesApi: MessagesApi, implicit val mail:MailerClient) extends Controller with ConnectionSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Connection].fold(
      errors => badRequest(errors.mkString),
      connection => {
        val newConnection = if(request.user.userType == UserType.OWNER) connection.copy(companyId = Some(request.user.companyId)) else connection
        Connections.insert(newConnection) match {
          case Left(e) =>  badRequest(e)
          case Right(id) => created (Some (newConnection), s"Created Connection with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    val connection = if(request.user.userType == UserType.OWNER) Connections.findById(id.toInt,Some(request.user.companyId)) else Connections.findById(id.toInt)
    if (connection.isDefined) ok(Json.toJson(connection), "Connection details") else notFound(s"Connection with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val connectionList = if(request.user.userType != UserType.ADMIN ) Connections.getAll(Some(request.user.companyId)) else Connections.getAll()
    ok(Json.toJson(connectionList), "List of connections")
  }

  def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Connection].fold(
      errors => badRequest(errors.mkString),
      connection => {
        if(!connection.id.isDefined || (connection.id.isDefined && id != connection.id.get)) validationError(connection,"Id provided in url and data are not equal")
        else {
          Connections.update(connection) match {
            case Left(e) => validationError(connection, e)
            case Right(r) => ok(Some(connection), s"Updated Connection with details" + connection)
          }
        }
      }
    )
  }

}