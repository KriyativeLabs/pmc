package controllers


import helpers.enums.UserType
import helpers.json.DashboardSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{CompanyStats, Notifications, Companies, Company}
import play.api._
import play.api.mvc._
import play.api.libs.json._
import security.{IsAuthenticated, PermissionCheckAction}

object DashboardController extends Controller with DashboardSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def dashboardData = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val result = Companies.dashboardData(request.user.companyId)
    println(result)
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