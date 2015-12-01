package models

import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Json
import com.github.tototoshi.slick.JdbcJodaSupport._
import slick.driver.PostgresDriver.api._
import utils.APIException

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
  private lazy val customerQuery = TableQuery[CustomersTable]
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

  def generateCompanyStats(companyId:Int):Int ={
    val lastMonth = DateTime.now().minusMonths(1).withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)

    val paymentQuery = paymentsQuery.filter(x => x.companyId === companyId && x.paidOn > lastMonth && x.paidOn < DateTime.now().withDayOfMonth(1)).map(_.paidAmount).sum
    val totalAmountCollected = DatabaseSession.run(paymentQuery.result).asInstanceOf[Option[Int]]

    val closingBalanceQuery = customerQuery.filter(x => x.companyId === companyId && x.balanceAmount > 0).map(_.balanceAmount).sum
    val closingBalance = DatabaseSession.run(closingBalanceQuery.result).asInstanceOf[Option[Int]]

    val resultQuery = companyStatisticsQuery returning companyStatisticsQuery.map(_.id) += CompanyStatistics(None,companyId,lastMonth,totalAmountCollected.getOrElse(0), closingBalance.getOrElse(0))
    try {
      DatabaseSession.run(resultQuery).asInstanceOf[Int]
    } catch {
      case e: Exception => {
        Logger.error(s"Failed to update company statistics:$companyId",e)
        throw new APIException(s"Failed to update company statistics:$companyId")
      }
    }
  }
}