package controllers

import javax.inject.Inject

import adapters.utils.{MSORequest, MSORequestProcessor}
import helpers.enums.{ConnectionStatus, ResponseStatus, MSOType, UserType}
import helpers.json.ConnectionSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Companies, Connection, Connections}
import play.api._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import security.{IsAuthenticated, PermissionCheckAction}

import scala.concurrent.Future

class ConnectionsController @Inject()(implicit val messagesApi: MessagesApi, implicit val mail: MailerClient) extends Controller with ConnectionSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  val FUTURE_TIMEOUT = 30

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Connection].fold(
      errors => badRequest(errors.mkString),
      connection => {
        val newConnection = if (request.user.userType == UserType.OWNER) connection.copy(companyId = Some(request.user.companyId)) else connection
        Connections.insert(newConnection) match {
          case Left(e) => badRequest(e)
          case Right(id) => created(Some(newConnection), s"Created Connection with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    val connection = if (request.user.userType == UserType.OWNER) Connections.findById(id.toInt, Some(request.user.companyId)) else Connections.findById(id.toInt)
    if (connection.isDefined) ok(Json.toJson(connection), "Connection details") else notFound(s"Connection with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val connectionList = if (request.user.userType != UserType.ADMIN) Connections.getAll(Some(request.user.companyId)) else Connections.getAll()
    ok(Json.toJson(connectionList), "List of connections")
  }

  def update(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Connection].fold(
      errors => badRequest(errors.mkString),
      connection => {
        if (!connection.id.isDefined || (connection.id.isDefined && id != connection.id.get)) validationError(connection, "Id provided in url and data are not equal")
        else {
          Connections.update(connection) match {
            case Left(e) => validationError(connection, e)
            case Right(r) => ok(Some(connection), s"Updated Connection with details" + connection)
          }
        }
      }
    )
  }

  def toggleStatus(id: Int, activate: Boolean) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>

    Connections.findById(id, Some(request.user.companyId)) match {
      case Some(connection) => {
        val company = Companies.findById(request.user.companyId).get

        val result = MSORequestProcessor.submit(MSORequest(connection.id.get, request.user.companyId, connection.setupBoxId, activate,
          company.msoType, company.cred1.getOrElse(""), company.cred2.getOrElse(""), company.cred3.getOrElse(""), company.cred4.getOrElse("")))

        result.status match {
          case ResponseStatus.SUCCESS if activate => Connections.updateMSOStatus(id, ConnectionStatus.ACTIVE); ok(None, "Successfully Activated!")
          case ResponseStatus.SUCCESS if !activate => Connections.updateMSOStatus(id, ConnectionStatus.IN_ACTIVE); ok(None, "Successfully De-Activated!")
          case ResponseStatus.ERROR => failed(result.errorMessage.getOrElse(""))
        }
      }
      case None => {
        forbidden("Connection not found")
      }
    }
  }

}