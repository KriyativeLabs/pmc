package helpers.json

import models.Area
import play.api.libs.json._

trait AreaSerializer {
  implicit val areaWriter = new Writes[Area] {
    def writes(area: Area) = Json.obj(
      "id" -> (if(area.id.isDefined) area.id.get else ""),
      "name" -> area.name,
      "code" -> area.code,
      "city" -> area.city,
      "idSequence" -> area.idSequence,
      "companyId" -> area.companyId
    )
  }

  implicit val areaListWriter = new Writes[List[Area]] {
    def writes(areaList: List[Area]):JsValue = {
      JsArray(areaList.map(area => areaWriter.writes(area)).toList)
    }
  }

}
