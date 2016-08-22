package v1.helpers

import org.json4s._
import org.json4s.jackson.Serialization._
import play.api.libs.json.{Format, Json, JsValue}

trait Base extends CommonUtil {
  def toJson: String
}

case class Meta(host: String, method: String, path: String, uri: String) extends Base {
  override def toJson: String = write(this)
}

case class Response(status: String, statusCode: Int, meta: Meta,
                    recordCount: Option[Int] = None,
                    data: Option[JValue] = None, message: String) extends Base {
  override def toJson: String = write(this)
}

trait JsonBase extends CommonUtil {
  def toJsValue: JsValue
}

case class JsonMeta(host: String, method: String, path: String, uri: String) extends JsonBase {
  override def toJsValue: JsValue = Json.toJson[JsonMeta](this)
}

object JsonMeta {
  implicit val metaFormat: Format[JsonMeta] = Json.format[JsonMeta]
}

case class JsonResponse(status: String, statusCode: Int, meta: JsonMeta,
                        data: JsValue, message: String) extends JsonBase {
  override def toJsValue: JsValue = Json.toJson(this)
}

object JsonResponse {
  implicit val responseFormat: Format[JsonResponse] = Json.format[JsonResponse]
}