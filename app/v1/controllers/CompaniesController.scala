package v1.controllers

import javax.inject.Inject

import helpers.enums.UserType
import helpers.{CommonUtil, ResponseHelper}
import v1.models.{Companies, Company}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.mailer.MailerClient
import play.api.mvc.Controller
import security.{IsAuthenticated, PermissionCheckAction}

class CompaniesController @Inject()(implicit val messagesApi: MessagesApi, implicit val mail:MailerClient)  extends Controller with CommonUtil with ResponseHelper {
  val logger = Logger(this.getClass)

  def create() = (IsAuthenticated andThen PermissionCheckAction(UserType.AGENT))(parse.json) { implicit request =>
    request.body.validate[Company].fold(
      errors => badRequest(errors.mkString),
      company => {
        Companies.insert(company) match {
          case Left(e) =>  badRequest(e)
          case Right(id) => created (Some(company), s"Created Company with id:$id")
        }
      }
    )
  }

  def find(id: Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER)) { implicit request =>
    Companies.findById(id.toInt) match {
      case Some(company) if (request.user.userType == UserType.ADMIN)  || (company.id.get == request.user.companyId) => ok(Json.toJson(company), "Company details")
      case _ => notFound(s"Company with $id not found")
    }
  }

  def all() = (IsAuthenticated andThen PermissionCheckAction(UserType.ADMIN)) { implicit request =>
    val companyList = Companies.getAll
    ok(Json.toJson(companyList), "List of companies")
  }

  def update(id:Int) = (IsAuthenticated andThen PermissionCheckAction(UserType.OWNER))(parse.json) { implicit request =>
    request.body.validate[Company].fold(
      errors => badRequest(errors.mkString),
      company => {
        if(!company.id.isDefined || (company.id.isDefined && id != company.id.get)) validationError(company,"Id provided in url and data are not equal")
        else if (id != request.user.companyId && request.user.userType != UserType.ADMIN) badRequest("Not Authorized to update")
        else {
          Companies.update(company) match {
            case Left(e) => validationError(company, e)
            case Right(no) => ok(Some(company), s"Successfully Updated!")
          }
        }
      }
    )
  }

}