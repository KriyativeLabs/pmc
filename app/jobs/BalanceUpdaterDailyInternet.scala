package jobs

import helpers.enums.SmsType
import models._
import org.joda.time.{DateTime, Months}
import org.quartz.{Job, JobExecutionContext}
import play.api.Logger

class BalanceUpdaterDailyInternet extends Job {
  override def execute(context: JobExecutionContext) {
    Companies.getAll.filter(!_.isCableNetwork).foreach({ company =>
      val customers = scala.collection.mutable.ArrayBuffer.empty[Int]
      if (DateTime.now().dayOfMonth().get() == 1) {
        Logger.info(s"Generating company Statistics for company:${company.id}")
        CompanyStats.generateCompanyStats(company.id.get)
      }
      Logger.info(s"Bill Generation started for company:${company.id}")
      val now = DateTime.now().plusDays(1)
      try {
        val plans = Plans.getAll(company.id).map(x => x.id -> x).toMap
        (if (company.lastBillGeneratedOn.getOrElse(DateTime.now()).monthOfYear() != now.monthOfYear() && Companies.startBilling(company.id.get)) {
          Customers.getAll(company.id.get)
        } else if (company.billStatus != Some(true)) {
          Customers.getAll(company.id.get, company.customerSeqNo.getOrElse(0))
        } else {
          Customers.getAll(company.id.get)
        }).foreach({ customer =>
          val p = customer._2.planId
            if (customer._2.status.toUpperCase == "ACTIVE") {
              val plan = plans.get(Some(p)).get

              val iDateCondition = if (now.getMonthOfYear == 2) {
                if (List(31, 30, 29).contains(customer._2.installationDate.getDayOfMonth)) {
                  28 == now.getDayOfMonth
                } else {
                  customer._2.installationDate.getDayOfMonth == now.getDayOfMonth
                }
              } else {
                if (customer._2.installationDate.getDayOfMonth == 31) {
                  30 == now.getDayOfMonth
                } else {
                  customer._2.installationDate.getDayOfMonth == now.getDayOfMonth
                }
              }

              if(Months.monthsBetween(customer._2.installationDate, now).getMonths !=0 && (Months.monthsBetween(customer._2.installationDate, now).getMonths % 1) == 0 && iDateCondition) {
                Logger.info("Updating Customer Id:" + customer._1.id + " with bill amount:" + (plan.amount - customer._2.discount))
                if (Customers.updateAmount(customer._1.id.get, customer._2.id.get, customer._2.discount - plan.amount)) {
                  Companies.updateCustomerSeqNo(company.id.get, customer._1.id.get)
                  customers += customer._1.id.get
                }
              }
            }
        })
        Companies.endBilling(company.id.get)
        val customersSmsCompleted = scala.collection.mutable.HashSet.empty[Int]
        Customers.getAll(company.id.get).map(_._1).filter(c => customers.contains(c.id.get)).foreach({ customer =>
          if(!customersSmsCompleted.contains(customer.id.get)){
            customersSmsCompleted += customer.id.get
            if (customer.balanceAmount > 0 && customer.mobileNo.isDefined) {
              val message = s"Dear Customer, You internet connection's balance amount is:${customer.balanceAmount}. Please pay to our agent to avoid disconnection."
              SmsGateway.sendSms(message, customer.mobileNo, company, SmsType.BALANCE_REMINDER)
            }
          }
        })
      } catch {
        case e: Exception => Logger.info(s"Exception caught in processing bill generation for company:${company.id}", e)
      }
    })
  }
}