package models

import helpers.enums.SmsType.SmsType
import play.api.{Logger, Play}
import play.api.libs.json.Json
import security.LoggedInUser
import play.api.mvc._
import play.api.libs.ws._
import utils.APIException
import play.api.libs.ws.ning._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class Sms(smsType: String, message: String)

object Sms {
  implicit val fmt = Json.format[Sms]
}

object SmsGateway {
  val logger = Logger(this.getClass)
  implicit val sslClient = NingWSClient()
  val smsUrl = Play.current.configuration.getString("sms.url").getOrElse(throw new APIException("SMS Settings not found in conf"))

  def send(sms: Sms)(implicit loggedInUser: LoggedInUser): Either[String, String] = {
    val company = Companies.findById(loggedInUser.companyId).get
    val customerList = sms.smsType.toLowerCase match {
      case "unpaid" => Customers.getAllWithFilters(loggedInUser.companyId, Some(true), Some(false), None)
      case "paid" => Customers.getAllWithFilters(loggedInUser.companyId, Some(false), Some(false), None)
      case "all" => Customers.getAllWithFilters(loggedInUser.companyId, None, Some(false), None)
    }
    sendSms(sms.message, customerList.flatMap(_.customer.mobileNo), company)
  }

  def sendSms(message: String, contactNo: Option[Long], company: Company): Either[String, String] = {
    if (contactNo.isDefined && company.smsEnabled) {
      try {
        val url = smsUrl.replace("%%CONTACTS%%", contactNo.get.toString).replace("%%MSG%%", message)
        val response = WS.clientUrl(url).get()
        Companies.updateSMSCount(company.id.get, 1)
        logger.info(url)
        //Await.result(response,Duration.Inf)
        Right("Sent")
      } catch {
        case e: Throwable => e.printStackTrace(); Left("Error" + e.getMessage)
      }
    } else {
      Left("Not Sent")
    }
  }

  def sendSms(message: String, contactNos: Vector[Long], company: Company): Either[String, String] = {
    if (contactNos.length > 0 && company.smsEnabled) {
      contactNos.distinct.sliding(20,20).foreach({ cNos =>
        try {
          val url = smsUrl.replace("%%CONTACTS%%", cNos.mkString(",")).replace("%%MSG%%", message)
          val response = WS.clientUrl(url).get()

          logger.info(url)
          //Await.result(response,Duration.Inf)
          Right("Sent")
        } catch {
          case e: Throwable => e.printStackTrace(); Left("Error" + e.getMessage)
        }
      })
      Companies.updateSMSCount(company.id.get, contactNos.length)
      Right("Sent")
    } else {
      Left("Not Sent")
    }
  }
}
