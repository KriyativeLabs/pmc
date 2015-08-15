package controllers

import helpers.enums.UserType
import helpers.json.PlanSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Plans, Plan}
import play.api.libs.json._
import security.{IsAuthenticated, PermissionCheckAction}
import play.api._
import play.api.mvc._

object SmsController extends Controller with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def sendSms() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val planList = if(request.user.userType != UserType.ADMIN ) Plans.getAll(Some(request.user.companyId)) else Plans.getAll()
    ok(Json.toJson(planList), "List of plans")
  }
}