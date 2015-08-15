package helpers.json

import models.Customer
import play.api.libs.json._

trait CustomerSerializer {
  implicit val customerWriter = new Writes[Customer] {
    def writes(customer: Customer) = Json.obj(
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

  implicit val customerListWriter = new Writes[List[Customer]] {
    def writes(customerList: List[Customer]):JsValue = {
      JsArray(customerList.map(customer => customerWriter.writes(customer)).toList)
    }
  }

}
