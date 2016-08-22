package v1.controllers


import javax.inject.Inject

import helpers.enums.UserType
import v1.helpers.json.DashboardSerializer
import v1.helpers.{CommonUtil, ResponseHelper}
import v1.models.{Companies, CompanyStats, Notifications}
import play.api._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.mailer.MailerClient
import play.api.mvc._
import security.{IsAuthenticated, PermissionCheckAction}

class DashboardController @Inject()(implicit val messagesApi: MessagesApi, implicit val mail:MailerClient) extends Controller with DashboardSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def dashboardData = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val result = Companies.dashboardData(request.user.companyId)
    ok(Json.toJson(result),"DashboardData")
  }

  def notifications = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    implicit val loggedInUser = request.user
    val result = Notifications.all()
    ok(Json.toJson(result),"Notifications")
  }

  def monthlyStatistics = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val result = CompanyStats.getLatestByCount(request.user.companyId)
    ok(Json.toJson(result),"Monthly Statistics")
  }

  def agentStatistics = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val result = CompanyStats.getAgentStatistics(request.user.companyId)
    ok(Json.toJson(result),"Agent Statistics")
  }

}