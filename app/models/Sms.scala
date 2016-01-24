package models

import helpers.enums.SmsType.SmsType
import play.api.{Logger, Play}
import play.api.libs.json.Json
import security.LoggedInUser
import play.api.mvc._
import play.api.libs.ws._
import utils.APIException
import play.api.libs.ws.ning._

case class Sms(smsType: String, message: String)

object Sms {
  implicit val fmt = Json.format[Sms]
}

object SmsGateway {
  val logger = Logger(this.getClass)
  implicit val sslClient = NingWSClient()
  val smsUrl = Play.current.configuration.getString("sms.url").getOrElse(throw new APIException("SMS Settings not found in conf"))

  def send(sms: Sms)(implicit loggedInUser: LoggedInUser): Either[String, String] = {
    Right("Sent")
  }

  def sendSms(message: String, contactNo: Option[Long]): Either[String, String] = {
    if (contactNo.isDefined) {
      try {
        val url = smsUrl.replace("%%CONTACTS%%", contactNo.get.toString).replace("%%MSG%%", message)
        val response = WS.clientUrl(url).get()
        logger.info(url)
        Right("Sent")
      } catch {
        case e: Throwable => e.printStackTrace(); Left("Error" + e.getMessage)
      }
    } else {
      Right("Not Sent")
    }
  }
}
