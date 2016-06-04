package controllers

import helpers.enums.UserType
import helpers.json.CompanySerializer
import helpers.{CommonUtil, ResponseHelper}
import models.{Companies, Company}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Controller
import security.{IsAuthenticated, PermissionCheckAction}

object CompaniesController extends Controller with CompanySerializer with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[Company].fold(
      errors => badRequest(errors.mkString),
      company => {
        Companies.insert(company) match {
          case Left(e) =>  badRequest(e)
          case Right(id) => created (Some (company), s"Created Company with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>

    val companyDao = Companies.findById(id.toInt)
    if (companyDao.isDefined) ok(Json.toJson(companyDao), "Company details") else notFound(s"Company with $id not found")
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT)) { implicit request =>
    val companyList = Companies.getAll
    ok(Json.toJson(companyList), "List of companies")
  }

  def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[Company].fold(
      errors => badRequest(errors.mkString),
      company => {
        if(!company.id.isDefined || (company.id.isDefined && id != company.id.get)) validationError(company,"Id provided in url and data are not equal")
        else {
          Companies.update(company) match {
            case Left(e) => validationError(company, e)
            case Right(id) => ok(Some(company), s"Updated Company with details" + company)
          }
        }
      }
    )
  }

}