package helpers.json

import models.{User, Company}
import play.api.libs.json._

trait UserSerializer {
  implicit val userWriter = new Writes[User] {
    def writes(user: User) = Json.obj(
      "id" -> (if(user.id.isDefined) user.id.get else ""),
      "name" -> user.name,
      "companyId" -> user.companyId,
      "loginId" -> user.loginId,
      "contactNo" -> user.contactNo,
      "email" -> user.email,
      "accountType" -> user.accountType,
      "address" -> user.address,
      "password" -> user.password,
      "status" -> user.status
    )
  }

  implicit val userListWriter = new Writes[List[User]] {
    def writes(userList: List[User]):JsValue = {
      JsArray(userList.map(user => userWriter.writes(user)).toList)
    }
  }

}
