package helpers.enums

object ResponseStatus extends Enumeration {
  type ResponseStatus = Value
  val SUCCESS = Value("SUCCESS")
  val ERROR = Value("ERROR")
}