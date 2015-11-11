package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import security.LoggedInUser
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.JdbcJodaSupport._
import utils.EntityNotFoundException

case class Notification(id: Option[Int], notification: String, gotOn: DateTime, companyId: Int)

object Notification {
  implicit val fmt = Json.format[Notification]
}

class NotificationsTable(tag: Tag) extends Table[Notification](tag, "notifications") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def notification = column[String]("notification")

  def gotOn = column[DateTime]("got_on")

  def companyId = column[Int]("company_id")

  def * = (id.?, notification, gotOn, companyId) <>((Notification.apply _).tupled, Notification.unapply _)
}

object Notifications {
  private lazy val notificationsQuery = TableQuery[NotificationsTable]

  def all()(implicit loggedInUser: LoggedInUser): Vector[Notification] = {
    val yesterday = DateTime.now().minusDays(1).withTime(23, 59, 59, 999)
    val query = notificationsQuery.filter(x => x.companyId === loggedInUser.companyId && x.gotOn > yesterday).sortBy(_.gotOn.desc).take(5)
    DatabaseSession.run(query.result).asInstanceOf[Vector[Notification]]
  }

  def insert(notification: Notification): Either[String, Int] = {
    val resultQuery = notificationsQuery returning notificationsQuery.map(_.id) += notification
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def createNotification(notification:String, userId:Int)(implicit loggedInUser: LoggedInUser):Either[String, Int] = {

    val user = Users.findById(userId).getOrElse(throw EntityNotFoundException(s"User not found with id:$userId"))
    insert(Notification(None,s"$notification by ${user.name}",DateTime.now(),loggedInUser.companyId))
  }


}