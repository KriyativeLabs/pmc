package v1.helpers.json

import v1.models.{Notification, Payment}
import play.api.libs.json._

trait NotificationSerializer {
  implicit val notificationWriter = new Writes[Notification] {
    def writes(notification: Notification) = Json.obj(
      "id" -> (if(notification.id.isDefined) notification.id.get else ""),
      "notification" -> notification.notification,
      "gotOn" -> notification.gotOn.toString("yyyy-MM-dd"),
      "companyId" -> notification.companyId
    )
  }

  implicit val notificationListWriter = new Writes[Vector[Notification]] {
    def writes(notificationList: Vector[Notification]):JsValue = {
      JsArray(notificationList.map(notification => notificationWriter.writes(notification)).toList)
    }
  }
}