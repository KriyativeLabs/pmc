package models

import play.api.libs.Codecs
import play.api.libs.json.Json
import security.LoggedInUser
import slick.driver.PostgresDriver.api._


case class LoginCase(loginId:String, password:String)
object  LoginCase{
  implicit val fmt = Json.format[LoginCase]
}

case class PasswordChange(oldPassword:String,newPassword:String)

object PasswordChange {
  implicit val fmt = Json.format[PasswordChange]
}

case class User(id:Option[Int],name:String,companyId:Int,loginId:String, password:String, contactNo:Long,
                   email:Option[String], accountType:String, address:String)
object User {
  implicit val fmt = Json.format[User]
}



class UsersTable(tag: Tag) extends Table[User](tag, "users"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def companyId = column[Int]("company_id")
  def loginId = column[String]("login_id")
  def password = column[String]("password")
  def contactNo = column[Long]("contact_no")
  def email = column[Option[String]]("email")
  //def accountType = column[UserType]("account_type")
  def accountType = column[String]("account_type")
  def address = column[String]("address")

  def * = (id.?,name,companyId,loginId,password,contactNo,email,accountType,address) <> ((User.apply _).tupled, User.unapply _)
}

object Users {
  private lazy val userQuery = TableQuery[UsersTable]

  def insert(user: User): Either[String, Int] = {
    val password = user.password
    val encryptPass = user.copy(password = Codecs.md5(password.getBytes))
    val resultQuery = userQuery returning userQuery.map(_.id) += encryptPass
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def login(login:LoginCase): Either[Boolean,User] = {
    val filterQuery = userQuery.filter(l => l.loginId === login.loginId && l.password === Codecs.md5(login.password.getBytes))
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[User]] match {
      case Some(x) => Right(x)
      case None => Left(false)
    }
  }

  def updatePassword(id:Int, oldPassword:String, newPassword:String): Either[String, Int] = {
    val updateQuery = userQuery.filter(x => x.id === id && x.password === Codecs.md5(oldPassword.getBytes)).map(c => c.password)
      .update(Codecs.md5(newPassword.getBytes))
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(user: User): Either[String, Int] = {
    val updateQuery = userQuery.filter(_.id === user.id).map(c => (c.name, c.contactNo, c.email, c.accountType, c.address))
      .update(user.name, user.contactNo, user.email, user.accountType, user.address)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findById(id: Int)(implicit loggedInUser: LoggedInUser): Option[User] = {
    val filterQuery = userQuery.filter(x => x.id === id && x.companyId === loggedInUser.companyId)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[User]]
  }

  def getAll()(implicit loggedInUser: LoggedInUser): Vector[User] = {
    val filterQuery = userQuery.filter(x => x.companyId === loggedInUser.companyId && !(x.id === loggedInUser.userId) )
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[User]]
  }
}
