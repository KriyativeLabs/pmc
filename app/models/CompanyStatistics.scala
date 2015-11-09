package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import com.github.tototoshi.slick.JdbcJodaSupport._
import slick.driver.PostgresDriver.api._

case class CompanyStatistics(id: Option[Int], companyId: Int, month: DateTime, collectedAmount: Int, closingBalance: Int)

object CompanyStatistics {
  implicit val fmt = Json.format[CompanyStatistics]
}

class CompanyStatisticsTable(tag: Tag) extends Table[CompanyStatistics](tag, "company_statistics") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def companyId = column[Int]("company_id")

  def month = column[DateTime]("month")

  def collectedAmount = column[Int]("collected_amount")

  def closingBalance = column[Int]("closing_balance")

  def * = (id.?, companyId, month, collectedAmount, closingBalance) <>((CompanyStatistics.apply _).tupled, CompanyStatistics.unapply _)
}

object CompanyStats {
  private lazy val companyStatisticsQuery = TableQuery[CompanyStatisticsTable]
  private lazy val paymentsQuery = TableQuery[PaymentsTable]
  private lazy val agentsQuery = TableQuery[UsersTable]

  def getLatestByCount(companyId: Int, count: Int = 5): Vector[CompanyStatistics] = {
    val filterQuery = companyStatisticsQuery.filter(x => x.companyId === companyId).sortBy(_.id.desc).take(count)
    DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[CompanyStatistics]]
  }

  def getAgentStatistics(companyId: Int):Map[String, Int] = {
    val lastMonthDate = DateTime.now().minusMonths(1).dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999)
    val filterQuery = for {
      (payment, agent) <- paymentsQuery.filter(x => x.companyId === companyId && x.paidOn > lastMonthDate) join agentsQuery on (_.agentId === _.id)
    } yield (payment, agent)
    val queryResult = DatabaseSession.run(filterQuery.result).asInstanceOf[Vector[(Payment, User)]]
    queryResult.groupBy(_._1.agentId).map(x => x._2(0)._2.name -> x._2.map(y => y._1.paidAmount).sum)
  }

}