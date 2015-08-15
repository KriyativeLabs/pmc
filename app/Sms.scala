
import helpers.enums.SmsType
import helpers.enums.SmsType.SmsType
import play.api.Play
import play.api.libs.json.{Reads, Json}
import utils.EnumUtils

case class Sms(msg:String,smsType:SmsType,companyId:Int)

object Sms {
  implicit val smsTypeEnumReads: Reads[SmsType.Value] = EnumUtils.enumReads(SmsType)
  implicit val fmt = Json.format[Sms]
}

object SmsService {
  val smsGateway = Play.current.configuration.getString("sms.gateway").get
  val smsGatewayUsername = Play.current.configuration.getString("sms.gateway.username").get
  val smsGatewayPassword = Play.current.configuration.getString("sms.gateway.password").get

  def sendSms(msg:String,mobileNum:Long) = {

  }

  def sendSms(msg:String,mobileNums:List[Long]) = {

  }
}