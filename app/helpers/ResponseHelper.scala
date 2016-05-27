package helpers

import org.json4s.{NoTypeHints, Extraction}
import org.json4s.jackson.Serialization
import play.api.Play
import play.api.http.Status._
import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc._
import play.mvc.Http.MimeTypes

trait ResponseHelper{// extends CommonUtil {

  def headers = List(
    "Access-Control-Allow-Origin" -> "*",
    "Access-Control-Allow-Methods" -> "GET, POST, OPTIONS, DELETE, PUT",
    "Access-Control-Allow-Headers" -> "Content-Type, Authorization, X-Requested-With"
  )
  private implicit val formats = Serialization.formats(NoTypeHints)
  val corsEnabled = false //Play.current.configuration.getString("cors.enabled").getOrElse("false").toBoolean

  def ok(data: Option[Any], message: String)(implicit request: RequestHeader): Result = {
    val result = Response("OK", OK, meta, data = Some(Extraction.decompose(data)), message = message)
    val res = Ok(result.toJson).as(MimeTypes.JSON)
    println(corsEnabled)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def ok(data: JsValue, message: String)(implicit request: RequestHeader): Result = {
    val result = JsonResponse("OK", OK, metaJson, data, message)
    val res = Ok(result.toJsValue).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def failed(message: String)(implicit request: RequestHeader): Result = {
    val result = Response("FAILED", NOT_MODIFIED,meta, message = message)
    val res = Results.BadRequest(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def created(data: JsValue, message: String)(implicit request: RequestHeader): Result = {
    val result = JsonResponse("CREATED", CREATED, metaJson, data, message)
    val res = Created(result.toJsValue).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def created(data: Option[Any], message: String)(implicit request: RequestHeader): Result = {
    val result = Response("CREATED", CREATED, meta, data = Some(Extraction.decompose(data)), message = message)
    val res = Created(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def internalServerError(message: String)(implicit request: RequestHeader): Result = {
    val result = Response("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, meta, data = None, message = message)
    val res = InternalServerError(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def notFound(message: String)(implicit request: RequestHeader): Result = {
    val result = Response("NOT_FOUND", NOT_FOUND, meta, data = None, message = message)
    val res = Results.NotFound(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def meta(implicit request: RequestHeader): Meta = Meta(request.host, request.method, request.path, request.uri)

  def metaJson(implicit request: RequestHeader): JsonMeta = JsonMeta(request.host, request.method, request.path, request.uri)

  def BadRequest(message: String)(implicit request: RequestHeader): Result = {
    val result = Response("BAD REQUEST", BAD_REQUEST, meta, message = message)
    val res = Results.BadRequest(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def unAuthorized(message: String)(implicit request: RequestHeader): Result = {
    val result = Response("UNAUTHORIZED", UNAUTHORIZED, meta, message = message)
    val res = Unauthorized(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  // http://stackoverflow.com/questions/3290182/rest-http-status-codes-for-failed-validation-or-invalid-duplicate
  def validationError(data: Any, message: String)(implicit request: RequestHeader): Result = {
    val result = Response("VALIDATION ERROR", BAD_REQUEST, meta, data = Some(Extraction.decompose(data)), message = message)
    val res = Results.BadRequest(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

  def forbidden(message: String)(implicit request: RequestHeader): Result = {
    val result = Response("FORBIDDEN", FORBIDDEN, meta, message = message)
    val res = Forbidden(result.toJson).as(MimeTypes.JSON)
    if (corsEnabled) res.withHeaders(headers: _*) else res
  }

}
