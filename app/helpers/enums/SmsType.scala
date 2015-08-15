package helpers.enums

object SmsType extends Enumeration{
  type SmsType = Value
  val ALL = Value("ALL")
  val UNPAID = Value("UNPAID")
  val PAID = Value("PAID")
  val INDIVIDUAL = Value("INDIVIDUAL")
}
