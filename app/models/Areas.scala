package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class Area(id:Option[Int],name:String,code:String,city:String,idSequence:Int,companyId:Int)
object Area {
  implicit val fmt = Json.format[Area]
}

class AreasTable(tag: Tag) extends Table[Area](tag, "areas"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def code = column[String]("code")
  def city = column[String]("city")
  def idSequence = column[Int]("id_sequence")
  def companyId = column[Int]("company_id")

  def * = (id.?,name,code,city,idSequence,companyId) <> ((Area.apply _).tupled, Area.unapply _)
}

object Areas {
  private lazy val areaQuery = TableQuery[AreasTable]

  def insert(area: Area): Either[String, Int] = {
    val resultQuery = areaQuery returning areaQuery.map(_.id) += area
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(area: Area): Either[String, Int] = {
    val updateQuery = areaQuery.filter(x => x.id === area.id && x.companyId === area.companyId).
      map(p => (p.name, p.city)).
      update(area.name, area.city)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findById(id: Int,companyId:Option[Int]=None): Option[Area] = {
    val filterQuery = if(companyId.isDefined) areaQuery.filter(x => x.id === id && x.companyId === companyId.get) else areaQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Area]]
  }

  def getAll(companyId:Option[Int]=None): Vector[Area] = {
    val filterQuery = if(companyId.isDefined) areaQuery.filter(x => x.companyId === companyId.get) else areaQuery
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Area]]
  }
}