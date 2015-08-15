package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class Plan(id:Option[Int],name:String,noOfMonths:Int,amount:Int,companyId:Int)
object Plan {
  implicit val fmt = Json.format[Plan]
}

class PlansTable(tag: Tag) extends Table[Plan](tag, "plans"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def noOfMonths = column[Int]("no_of_months")
  def amount = column[Int]("amount")
  def companyId = column[Int]("company_id")
  def * = (id.?,name,noOfMonths,amount,companyId) <> ((Plan.apply _).tupled, Plan.unapply _)
}

object Plans {
  private lazy val planQuery = TableQuery[PlansTable]

  def insert(plan: Plan): Either[String, Int] = {
    val resultQuery = planQuery returning planQuery.map(_.id) += plan
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(plan: Plan): Either[String, Int] = {
    val updateQuery = planQuery.filter(x => x.id === plan.id && x.companyId === plan.companyId).map(p => (p.name, p.noOfMonths, p.amount)).update(plan.name, plan.noOfMonths, plan.amount)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findById(id: Int,companyId:Option[Int]=None): Option[Plan] = {
    val filterQuery = if(companyId.isDefined) planQuery.filter(x => x.id === id && x.companyId === companyId.get) else planQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Plan]]
  }

  def getAll(companyId:Option[Int]=None): Vector[Plan] = {
    val filterQuery = if(companyId.isDefined) planQuery.filter(x => x.companyId === companyId.get) else planQuery
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Plan]]
  }
}