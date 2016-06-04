package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import utils.APIException

case class Area(id: Option[Int], name: String, code: String, city: String, idSequence: Int, companyId: Int)

object Area {
  implicit val fmt = Json.format[Area]
}

class AreasTable(tag: Tag) extends Table[Area](tag, "areas") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def code = column[String]("code")

  def city = column[String]("city")

  def idSequence = column[Int]("id_sequence")

  def companyId = column[Int]("company_id")

  def * = (id.?, name, code, city, idSequence, companyId) <>((Area.apply _).tupled, Area.unapply _)
}

object Areas {
  private lazy val areaQuery = TableQuery[AreasTable]
  private lazy val customerQuery = TableQuery[CustomersTable]

  def insert(area: Area): Either[String, Int] = {
    val resultQuery = areaQuery returning areaQuery.map(_.id) += area
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(area: Area): Either[String, Int] = {
    val updateQuery = areaQuery.filter(x => x.id === area.id.get && x.companyId === area.companyId).
      map(p => (p.name, p.code, p.city)).
      update(area.name, area.code, area.city)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def findById(id: Int, companyId: Option[Int] = None): Option[Area] = {
    val filterQuery = if (companyId.isDefined) areaQuery.filter(x => x.id === id && x.companyId === companyId.get) else areaQuery.filter(x => x.id === id)
    DatabaseSession.run(filterQuery.result.headOption).asInstanceOf[Option[Area]]
  }

  def getAll(companyId: Option[Int] = None): Vector[Area] = {
    val filterQuery = if (companyId.isDefined) areaQuery.filter(x => x.companyId === companyId.get) else areaQuery
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[Area]]
  }

  def delete(areaId: Int, companyId: Int): Either[String, Int] = {
    val deleteQuery = areaQuery.filter(x => x.id === areaId && x.companyId === companyId).delete

    val custQuery = customerQuery.filter(x => x.areaId === areaId && x.companyId === companyId).size
    val count = DatabaseSession.run(custQuery.result).asInstanceOf[Int]

    if (count == 0) {
      try {
        Right(DatabaseSession.run(deleteQuery).asInstanceOf[Int])
      } catch {
        case e: Exception => Left(e.getMessage)
      }
    } else {
      Left(s"Area is associated with $count no of cutomers, Please change them before deleting the area")
    }
  }

  def getIdSequence(id: Int, companyId: Int): Either[String, String] = {
    val area = findById(id).getOrElse(throw new APIException(s"Cannot find area with id:$id"))
    val updateQuery = areaQuery.filter(x => x.id === id && x.companyId === companyId && x.idSequence === area.idSequence).map(_.idSequence).update(area.idSequence + 1)
    try {
      val result = DatabaseSession.run(updateQuery).asInstanceOf[Int]
      if (result > 0) {
        Right(area.code + "-" + area.idSequence)
      } else {
        Left(s"Cannot get id sequence for areaId:$id")
      }
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }
}