package controllers

import controllers.AreasController._
import helpers.enums.UserType
import helpers.json.PlanSerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Areas, Plans, Plan}
import play.api.libs.json._
import security.{IsAuthenticated, PermissionCheckAction}
import play.api._
import play.api.mvc._

object PlansController extends Controller with PlanSerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Plan].fold(
      errors => BadRequest(errors.mkString),
      plan => {
        val newPlan = if(request.user.userType == UserType.OWNER) plan.copy(companyId = request.user.companyId) else plan
        Plans.insert(newPlan) match {
          case Left(e) =>  BadRequest(e)
          case Right(id) => created (Some (newPlan), s"Created Plan with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    val plan = if(request.user.userType == UserType.OWNER) Plans.findById(id.toInt,Some(request.user.companyId)) else Plans.findById(id.toInt)
    if (plan.isDefined) ok(Json.toJson(plan), "Plan details") else notFound(s"Plan with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val planList = if(request.user.userType != UserType.ADMIN ) Plans.getAll(Some(request.user.companyId)) else Plans.getAll()
    ok(Json.toJson(planList), "List of plans")
  }

  def delete(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    Plans.delete(id.toInt,request.user.companyId) match {
      case Left(e) => failed(e)
      case Right(msg) => ok(None,"Successfully deleted Plan!")
    }
  }

  def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Plan].fold(
      errors => BadRequest(errors.mkString),
      plan => {
        if(!plan.id.isDefined || (plan.id.isDefined && id != plan.id.get)) validationError(plan,"Id provided in url and data are not equal")
        else {
          val newPlan = plan.copy(companyId = request.user.companyId)
          Plans.update(newPlan) match {
            case Left(e) => validationError(plan, e)
            case Right(r) => ok(Some(plan), s"Updated Plan with details:" + newPlan)
          }
        }
      }
    )
  }

}