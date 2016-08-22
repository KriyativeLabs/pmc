package v1.models

import helpers.enums.SmsType
import helpers.enums.SmsType.SmsType
import play.api.{Logger, Play}
import play.api.libs.json.Json
import security.LoggedInUser
import play.api.libs.ws._
import utils.APIException
import play.api.libs.ws.ning._

case class Sms(smsGroup: String, message: String)

object Sms {
  implicit val fmt = Json.format[Sms]
}

object SmsGateway {
  val logger = Logger(this.getClass)
  implicit val sslClient = NingWSClient()
  val smsUrl = Play.current.configuration.getString("sms.url").getOrElse(throw new APIException("SMS Settings not found in conf"))

  def send(sms: Sms, sType:SmsType)(implicit loggedInUser: LoggedInUser): Either[String, String] = {
    val company = Companies.findById(loggedInUser.companyId).get
    val customerList = sms.smsGroup.toLowerCase match {
      case "unpaid" => Customers.getAllWithFilters(loggedInUser.companyId, Some(true), Some(false), None)
      case "paid" => Customers.getAllWithFilters(loggedInUser.companyId, Some(false), Some(false), None)
      case "all" => Customers.getAllWithFilters(loggedInUser.companyId, None, Some(false), None)
    }
    sendSms(sms.message, customerList.flatMap(_.customer.mobileNo), company, sType)
  }

  def sendSms(message: String, contactNo: Option[Long], company: Company, sType:SmsType): Either[String, String] = {
    if (contactNo.isDefined && validate(company, sType)) {
      try {
        val url = smsUrl.replace("%%CONTACTS%%", contactNo.get.toString).replace("%%MSG%%", message)
        WS.clientUrl(url).get()
        Companies.updateSMSCount(company.id.get, 1)
        logger.info(url)
        Right("Sent")
      } catch {
        case e: Throwable => e.printStackTrace(); Left("Error" + e.getMessage)
      }
    } else {
      Left("Not Sent")
    }
  }

  def sendSms(message: String, contactNo: Option[Long], sType:SmsType): Either[String, String] = {
    try {
      val url = smsUrl.replace("%%CONTACTS%%", contactNo.get.toString).replace("%%MSG%%", message)
      WS.clientUrl(url).get()
      logger.info(url)
      Right("Sent")
    } catch {
      case e: Throwable => e.printStackTrace(); Left("Error" + e.getMessage)
    }
  }

  def sendSms(message: String, contactNos: Vector[Long], company: Company, sType:SmsType): Either[String, String] = {
    if (contactNos.length > 0 && validate(company, sType)) {
      contactNos.distinct.sliding(20, 20).foreach({ cNos =>
        try {
          val url = smsUrl.replace("%%CONTACTS%%", cNos.mkString(",")).replace("%%MSG%%", message)
          WS.clientUrl(url).get()
          logger.info(url)
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

  private def validate(company:Company, sType:SmsType):Boolean = {
    sType match {
      case SmsType.GENERAL => true
      case SmsType.BULK_SMS =>  company.smsEnabled && company.bulkSMS
      case SmsType.PAYMENT_SMS => company.smsEnabled && company.paymentSMS
      case SmsType.BALANCE_REMINDER => company.smsEnabled && company.balanceReminders
      case SmsType.SUBSCRIPTION_SMS => company.smsEnabled && company.subscriptionSMS
      case _ => company.smsEnabled
    }
  }
}
