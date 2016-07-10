package jobs

import models.{CompanyStats, Plans, Customers, Companies}
import org.joda.time.DateTime
import org.joda.time.Months
import org.quartz.{JobExecutionContext, Job}
import play.api.Logger

class BalanceUpdaterMonthlyCable extends Job {
  override def execute(context: JobExecutionContext) {
    Companies.getAll.filter(_.isCableNetwork).foreach({ company =>

      Logger.info(s"Generating company Statistics for company:${company.id}")
      CompanyStats.generateCompanyStats(company.id.get)

      Logger.info(s"Bill Generation started for company:${company.id}")
      val now = DateTime.now()
      try {
        val plans = Plans.getAll(company.id).map(x => x.id -> x).toMap
        (if (company.lastBillGeneratedOn.getOrElse(DateTime.now()).monthOfYear() != now.monthOfYear() && Companies.startBilling(company.id.get)) {
          Customers.getAll(company.id.get)
        } else if (company.billStatus != Some(true)) {
          //Customers.getAll(company.id.get, company.customerSeqNo.getOrElse(0))
          Customers.getAll(company.id.get)
        } else {
          List()
        }).foreach({ customer =>
            if (customer._2.status.toUpperCase == "ACTIVE") {
              val plan = plans.get(Some(customer._2.planId)).get
              if (Months.monthsBetween(customer._2.installationDate.withDayOfMonth(1), now.withDayOfMonth(2)).getMonths != 0 && (Months.monthsBetween(customer._2.installationDate.withDayOfMonth(1), now.withDayOfMonth(2)).getMonths % plan.noOfMonths) == 0) {
                Logger.info("Updating Customer Id:" + customer._1.id + " with bill amount:" + (plan.amount - customer._2.discount))
                if (Customers.updateAmount(customer._1.id.get, customer._2.id.get, customer._2.discount - plan.amount)) {
                  Companies.updateCustomerSeqNo(company.id.get, customer._1.id.get)
                }
              }
            }
        })
        Companies.endBilling(company.id.get)
      } catch {
        case e: Exception => Logger.info(s"Exception caught in processing bill generation for company:${company.id}", e)
      }
    })
  }
}
