package helpers.json

import models.Connection
import org.joda.time.DateTime
import play.api.libs.json._

trait ConnectionSerializer {
  implicit val connectionWriter = new Writes[Connection] {
    def writes(connection: Connection) = Json.obj(
      "id" -> (if(connection.id.isDefined) connection.id.get else ""),
      "customerId" -> connection.customerId,
      "setupBoxId" -> connection.setupBoxId,
      "planId" -> connection.planId,
      "discount" -> connection.discount,
      "installationDate" -> connection.installationDate,
      "status" -> connection.status,
      "cafId" -> connection.cafId,
      "idProof" -> connection.idProof,
      "companyId" -> connection.companyId
    )
  }

  implicit val connectionListWriter = new Writes[List[Connection]] {
    def writes(connectionList: List[Connection]):JsValue = {
      JsArray(connectionList.map(connection => connectionWriter.writes(connection)).toList)
    }
  }
}
