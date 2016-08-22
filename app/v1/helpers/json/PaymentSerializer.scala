package v1.helpers.json

import v1.models.{Credit, PaymentCapsule, Payment}
import org.joda.time.DateTime
import play.api.libs.json._

trait PaymentSerializer {
  implicit val paymentWriter = new Writes[Payment] {
    def writes(payment: Payment) = Json.obj(
      "id" -> (if(payment.id.isDefined) payment.id.get else ""),
      "customerId" -> payment.customerId,
      "paidAmount" -> payment.paidAmount,
      "discount" -> payment.discountedAmount,
      "paidOn" -> payment.paidOn.toString("yyyy-MM-dd"),
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

  implicit val paymentCapsuleWriter = new Writes[PaymentCapsule] {
    def writes(payment: PaymentCapsule) = Json.obj(
      "receiptNo" -> payment.receiptNo,
      "customerDetails" -> payment.customerDetails,
      "paidAmount" -> payment.paidAmount,
      "paidOn" -> payment.paidOn.toString("yyyy-MM-dd"),
      "remarks" -> (if(payment.remarks.isDefined) payment.remarks.get else ""),
      "agentDetails" -> payment.agentDetails
    )
  }

  implicit val paymentCapsuleListWriter = new Writes[List[PaymentCapsule]] {
    def writes(paymentList: List[PaymentCapsule]):JsValue = {
      JsArray(paymentList.map(payment => paymentCapsuleWriter.writes(payment)).toList)
    }
  }

  implicit val creditWriter:Writes[Credit] = new Writes[Credit] {
    override def writes(c: Credit): JsValue = Json.obj(
    "amount" -> c.amount,
    "creditedOn" -> c.creditedOn.toString("yyyy-MM"),
    "generatedOn" -> c.generatedOn.toString("yyyy-MM-dd")
    )
  }

  implicit val creditListWriter:Writes[Vector[Credit]] = new Writes[Vector[Credit]] {
    override def writes(o: Vector[Credit]): JsValue ={
      JsArray(o.map(credit => creditWriter.writes(credit)))
    }
  }

}