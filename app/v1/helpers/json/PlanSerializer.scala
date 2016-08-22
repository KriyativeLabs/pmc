package v1.helpers.json

import v1.models.Plan
import play.api.libs.json._

trait PlanSerializer {
  implicit val planWriter = new Writes[Plan] {
    def writes(plan: Plan) = Json.obj(
      "id" -> (if(plan.id.isDefined) plan.id.get else ""),
      "name" -> plan.name,
      "noOfMonths" -> plan.noOfMonths,
      "amount" -> plan.amount,
      "companyId" -> plan.companyId
    )
  }

  implicit val planListWriter = new Writes[List[Plan]] {
    def writes(planList: List[Plan]):JsValue = {
      JsArray(planList.map(plan => planWriter.writes(plan)).toList)
    }
  }

}
