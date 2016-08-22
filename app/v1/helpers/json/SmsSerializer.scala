package v1.helpers.json

import v1.models.{Sms, Area}
import play.api.libs.json._

trait SmsSerializer {
  implicit val smsWriter = new Writes[Sms] {
    def writes(sms: Sms) = Json.obj(
      "smsType" -> sms.smsGroup,
      "message" -> sms.message
    )
  }

  implicit val smsListWriter = new Writes[List[Sms]] {
    def writes(smsList: List[Sms]):JsValue = {
      JsArray(smsList.map(sms => smsWriter.writes(sms)).toList)
    }
  }

}
