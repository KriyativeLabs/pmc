package helpers.enums

object UserType extends Enumeration{
  type UserType = Value
  val ADMIN = Value("ADMIN")
  val OWNER = Value("OWNER")
  val AGENT = Value("AGENT")
}
