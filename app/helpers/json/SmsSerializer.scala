package helpers.json

import models.{Sms, Area}
import play.api.libs.json._

trait SmsSerializer {
  implicit val smsWriter = new Writes[Sms] {
    def writes(sms: Sms) = Json.obj(
      "smsType" -> sms.smsType,
      "message" -> sms.message
    )
  }

  implicit val smsListWriter = new Writes[List[Sms]] {
    def writes(smsList: List[Sms]):JsValue = {
      JsArray(smsList.map(sms => smsWriter.writes(sms)).toList)
    }
  }

}
