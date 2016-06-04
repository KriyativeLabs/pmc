package utils

import java.io.IOException

import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.libs.json.JsResultException
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future
import helpers.ResponseHelper

class ErrorHandler extends HttpErrorHandler with ResponseHelper{
  val logger: Logger = Logger(this.getClass)
  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    implicit val requestHeader = request
    Future.successful(
      exception match {
        case e: JsResultException =>
          logger.error(s"Error saving entity: - Validation errors ${e.errors}", e)
          validationError(e.errors.map { case (path, errorSeq) => (path.toString().substring(1), "Mandatory field missing or incorrect value")}, "Validation errors")
        case e: IOException =>
          logger.error(e.getMessage)
          internalServerError("A read/write exception occurred " + e.getMessage)
        case e: Throwable =>
          logger.error(s"Error in operation:", e)
          internalServerError(s"Error in operation: " + e.getMessage)
      })
  }
}