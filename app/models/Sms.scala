package models

import helpers.enums.SmsType.SmsType
import play.api.libs.json.Json
import security.LoggedInUser

case class Sms(smsType:String, message:String)
object Sms {
  implicit val fmt = Json.format[Sms]
}

object SmsGateway {
  def send(sms:Sms)(implicit loggedInUser:LoggedInUser):Either[String,String] = {
    println(sms)
    Right("Sent")
  }

  def sendSms(message:String,contactNo:Option[Long]):Either[String,String] = {
    Right("Sent")
  }
}
