package helpers

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import play.api.Logger
import play.api.mvc.{Action, Result, AnyContent, Request}
import security.{Authentication, LoggedInUser, AuthenticatedRequest}

trait CommonUtil {

  implicit val formats = Serialization.formats(NoTypeHints)
  def currentUser(implicit request: AuthenticatedRequest[Any]): LoggedInUser = {
    request.user
  }

}
