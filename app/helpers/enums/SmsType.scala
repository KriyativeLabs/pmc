package helpers.enums

object SmsType extends Enumeration{
  type SmsType = Value
  val GENERAL = Value("GENERAL")
  val BULK_SMS = Value("BULK_SMS")
  val PAYMENT_SMS = Value("PAYMENT_SMS")
  val BALANCE_REMINDER = Value("BALANCE_REMINDER")
  val SUBSCRIPTION_SMS = Value("SUBSCRIPTION_SMS")
}
