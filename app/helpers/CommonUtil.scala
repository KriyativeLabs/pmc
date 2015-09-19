package helpers

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import security.{AuthenticatedRequest, LoggedInUser}
import utils.PMCForkJoinPool

import scala.concurrent.ExecutionContext

trait CommonUtil {

  implicit val formats = Serialization.formats(NoTypeHints)
  implicit val executionContext = ExecutionContext.fromExecutorService(new PMCForkJoinPool())
  def currentUser(implicit request: AuthenticatedRequest[Any]): LoggedInUser = {
    request.user
  }

}
