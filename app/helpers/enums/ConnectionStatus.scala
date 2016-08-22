package helpers.enums

object ConnectionStatus extends Enumeration{
  type ConnectionStatus = Value
  val ACTIVE = Value("ACTIVE")
  val IN_ACTIVE = Value("IN_ACTIVE")
  val UNKNOWN = Value("UNKNOWN")
}
