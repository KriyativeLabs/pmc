package helpers.json

import models.Payment
import play.api.libs.json._

trait PaymentSerializer {
  implicit val paymentWriter = new Writes[Payment] {
    def writes(payment: Payment) = Json.obj(
      "id" -> (if(payment.id.isDefined) payment.id.get else ""),
      "customerId" -> payment.customerId,
      "amount" -> payment.paidAmount,
      "discount" -> payment.discountedAmount,
      "paidOn" -> payment.paidOn,
      "remarks" -> (if(payment.remarks.isDefined) payment.remarks.get else ""),
      "agentId" -> payment.agentId,
      "companyId" -> payment.companyId
    )
  }

  implicit val paymentListWriter = new Writes[List[Payment]] {
    def writes(paymentList: List[Payment]):JsValue = {
      JsArray(paymentList.map(payment => paymentWriter.writes(payment)).toList)
    }
  }
}