package security

import helpers.ResponseHelper
import helpers.enums.UserType
import helpers.enums.UserType.UserType
import org.joda.time.DateTime
import play.api.mvc._
import play.api.{Logger, Play}
import play.api.libs.Crypto
import play.mvc.Http.HeaderNames
import utils.{SessionExpiryException, AuthenticationException}

import scala.concurrent.Future
import java.util.Base64
import java.lang._


case class LoggedInUser(userId:Int,companyId:Int,expiryDay:Int,userType:UserType)

object LoggedInUser_1{
  private val user:ThreadLocal[Option[LoggedInUser]] = new InheritableThreadLocal[Option[LoggedInUser]]
  def set(u:LoggedInUser) : Unit = {
    println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+u)
    user.set(Some(u))
  }
  def reset() :Unit = {
    user.set(None)
  }
  def apply() : LoggedInUser = {
    println(user.get())
    user.get().getOrElse({
      throw new Exception("No User configured for thread =>" + Thread.currentThread().getId )
    })
  }
}

class AuthenticatedRequest[A](val user: LoggedInUser, request: Request[A]) extends WrappedRequest[A](request)

object Authentication {
  val logger: Logger = Logger(this.getClass)
  val delim = Play.current.configuration.getString("app.encryption.delimiter").getOrElse("#")
  val key = Play.current.configuration.getString("app.auth.key")
  val token = Play.current.configuration.getString("app.password.secret").getOrElse("6fPOPjhQofmmyY")
  if (!key.isDefined) {
    logger.error("encryption auth key not found ")
    throw new RuntimeException("Encryption key not found")
  }
  val encoder = Base64.getEncoder
  val decoder = Base64.getDecoder

  def encryptAuthHeader(userId:Int,companyId:Int,timeOut:Int,userType:UserType):String={
    val expiryDay = DateTime.now().getDayOfYear+timeOut
    val tokenString:String = userId+"#"+companyId+"#"+expiryDay+"#"+userType.toString
    new String(encoder.encode(Crypto.encryptAES(tokenString,key.get).getBytes))
  }

  def decryptAuthHeader(authHeader: String): Either[String, LoggedInUser] = {
    try {
      val decryptString = Crypto.decryptAES(new String(decoder.decode(authHeader)), key.get)
      val splitStr = decryptString.split(delim)
      if (splitStr.size < 4) {
        throw AuthenticationException("Authentication header is corrupted")
      }
      val intList = try {
        splitStr.take(3).map(_.toInt)
      } catch {
        case e: Exception => throw AuthenticationException("Authentication header is corrupted")
      }
      if (intList(2) < DateTime.now().getDayOfYear) {
        throw SessionExpiryException("Session Expired!")
      }
      Right(LoggedInUser(intList(0), intList(1), intList(2), UserType.withName(splitStr(3))))
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }
}

object IsAuthenticated extends ActionBuilder[AuthenticatedRequest] with ActionRefiner[Request, AuthenticatedRequest] with ResponseHelper{

  def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = Future.successful {
    try {
      val authString = request.headers.get(HeaderNames.AUTHORIZATION)
      if (!authString.isDefined) {
        throw new IllegalArgumentException("Authorization header is missing")
      }
      Authentication.decryptAuthHeader(authString.get) match {
        case Left(s) => Left(unAuthorized("Invalid user")(request))
        case Right(l) => {LoggedInUser_1.set(l);println(l);Right(new AuthenticatedRequest(l, request))}
      }
    } catch {
      case e: IllegalArgumentException => {
        println("UnAuthorized")
        Left(unAuthorized(e.getMessage)(request))
      }
    }
  }

}

class PermissionCheckAction(permissionType: UserType) extends ActionFilter[AuthenticatedRequest] with ResponseHelper{

  def filter[A](request: AuthenticatedRequest[A]) = Future.successful {
    request.user.userType match {
      case UserType.AGENT => if(permissionType == UserType.AGENT) None else Some(unAuthorized("You are unauthorized for this action")(request))
      case UserType.OWNER => if(permissionType == UserType.AGENT || permissionType == UserType.OWNER) None else Some(unAuthorized("You are unauthorized for this action")(request))
      case UserType.ADMIN => None
    }
  }
}
object PermissionCheckAction {

  def apply(permissionType: UserType): PermissionCheckAction = {
    new PermissionCheckAction(permissionType)
  }
}
