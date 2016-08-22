package v1.helpers

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

  def paginationAttributes(implicit request: AuthenticatedRequest[_]): (Option[String],Option[String], Option[Int], Option[Int]) = {
    val sortBy = request.getQueryString("sortBy")
    val sortOrder = request.getQueryString("sortOrder")
    val pageSize = request.getQueryString("pageSize") match {
      case Some(x) => if (x.forall(_.isDigit)) Some(x.toInt) else None
      case None => None
    }
    val pageNo = request.getQueryString("pageNo") match {
      case Some(x) => if (x.forall(_.isDigit)) Some(x.toInt) else None
      case None => None
    }

    (sortBy, sortOrder, pageSize, pageNo)
  }

}
