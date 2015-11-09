package helpers.json

import models.DashboardData
import play.api.libs.json._

trait DashboardSerializer {
  implicit val dashboardDataWriter = new Writes[DashboardData] {
    def writes(ddata: DashboardData) = Json.obj(
      "unpaidCustomers" -> ddata.unpaidCustomers,
      "totalCustomers" -> ddata.totalCustomers,
      "balanceAmount" -> ddata.balanceAmount,
      "amountCollected" -> ddata.amountCollected
    )
  }

}
