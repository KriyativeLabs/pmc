package models

import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._

case class AgentAreaMapping(id:Option[Int],agentId:Int,areaId:Int)
object AgentAreaMapping {
  implicit val fmt = Json.format[AgentAreaMapping]
}

class AgentAreaMappingTable(tag: Tag) extends Table[AgentAreaMapping](tag, "agent_area_mapping"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def agentId = column[Int]("agent_id")
  def areaId = column[Int]("area_id")

  def * = (id.?,agentId,areaId) <> ((AgentAreaMapping.apply _).tupled, AgentAreaMapping.unapply _)
}

object AgentAreaMappings {
  private lazy val agentAreaQuery = TableQuery[AgentAreaMappingTable]

  def insert(aam: AgentAreaMapping): Either[String, Int] = {
    val resultQuery = agentAreaQuery returning agentAreaQuery.map(_.id) += aam
    try {
      Right(DatabaseSession.run(resultQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }

  def update(aam: AgentAreaMapping): Either[String, Int] = {
    val updateQuery = agentAreaQuery.filter(x => x.id === aam.id).
      map(a => (a.agentId, a.areaId)).
      update(aam.agentId, aam.areaId)
    try {
      Right(DatabaseSession.run(updateQuery).asInstanceOf[Int])
    } catch {
      case e: Exception => Left(e.getMessage)
    }
  }
}