package utils

class APIException(message: String) extends RuntimeException(message)
case class DbFkException(message: String) extends APIException(message)
case class DbUniqueConstraintException(message: String) extends APIException(message)

case class AuthenticationException(message: String) extends APIException(message)
case class SessionExpiryException(message: String) extends APIException(message)
case class EntityNotFoundException(message: String) extends APIException(message)

case class AdapterException(message: String) extends APIException(message)
