package helpers.json

import models.CustomerCore
import play.api.libs.json._

trait CustomerSerializer {
  implicit val customerWriter = new Writes[CustomerCore] {
    def writes(customer: CustomerCore) = Json.obj(
      "id" -> (if(customer.id.isDefined) customer.id.get else ""),
      "name" -> customer.name,
      "mobileNo" -> customer.mobileNo,
      "emailId" -> customer.emailId,
      "address" -> customer.address,
      "companyId" -> customer.companyId,
      "areaId" -> customer.areaId,
      "houseNo" -> customer.houseNo,
      "balanceAmount" -> customer.balanceAmount
    )
  }

  implicit val customerListWriter = new Writes[List[CustomerCore]] {
    def writes(customerList: List[CustomerCore]):JsValue = {
      JsArray(customerList.map(customer => customerWriter.writes(customer)).toList)
    }
  }

}
