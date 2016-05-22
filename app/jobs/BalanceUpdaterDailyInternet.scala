package jobs

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
      val now = DateTime.now()
      try {
        val plans = Plans.getAll(company.id).map(x => x.id -> x).toMap
        (if (company.lastBillGeneratedOn.getOrElse(DateTime.now()).monthOfYear() != now.monthOfYear() && Companies.startBilling(company.id.get)) {
          Customers.getAll(company.id.get)
        } else if (company.billStatus != Some(true)) {
          Customers.getAll(company.id.get, company.customerSeqNo.getOrElse(0))
        } else {
          List()
        }).foreach({ customer =>
          customer.connection.map(_.planId) match {
            case Some(p) if customer.connection.map(_.status) == Some("ACTIVE") => {
              val plan = plans.get(Some(customer.connection.get.planId)).get
              if ((Months.monthsBetween(customer.connection.get.installationDate.withDayOfMonth(1), now.withDayOfMonth(1)).getMonths % plan.noOfMonths) == 0) {
                Logger.info("Updating Customer Id:" + customer.customer.id + " with bill amount:" + (plan.amount - customer.connection.get.discount))
                if (Customers.updateAmount(customer.customer.id.get, customer.connection.get.discount - plan.amount)) {
                  Companies.updateCustomerSeqNo(company.id.get, customer.customer.id.get)
                  customers += customer.customer.id.get
                }
              }
            }
            case _ => {
            }
          }
        })
        Companies.endBilling(company.id.get)
        val customersSmsCompleted = scala.collection.mutable.HashSet.empty[Int]
        Customers.getAll(company.id.get).map(_.customer).filter(c => customers.contains(c.id.get)).foreach({ customer =>
          if(!customersSmsCompleted.contains(customer.id.get)){
            customersSmsCompleted += customer.id.get
            if (customer.balanceAmount > 0 && customer.mobileNo.isDefined) {
              val message = s"Dear Customer, You internet connection pending balance is:${customer.balanceAmount}. Please pay to our agent to avoid disconnection."
              SmsGateway.sendSms(message, customer.mobileNo)
            }
          }
        })
      } catch {
        case e: Exception => Logger.info(s"Exception caught in processing bill generation for company:${company.id}", e)
      }
    })
  }
}