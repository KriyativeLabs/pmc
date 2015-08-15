package helpers.json

import models.Company
import play.api.libs.json._

trait CompanySerializer {
  implicit val companyWriter = new Writes[Company] {
    def writes(company: Company) = Json.obj(
      "id" -> (if(company.id.isDefined) company.id.get else ""),
      "name" -> company.name,
      "owner" -> company.owner,
      "contactNo" -> company.contactNo,
      "address" -> company.address
    )
  }

  implicit val companyListWriter = new Writes[List[Company]] {
    def writes(companyList: List[Company]):JsValue = {
      JsArray(companyList.map(company => companyWriter.writes(company)).toList)
    }
  }

}

