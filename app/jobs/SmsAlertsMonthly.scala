package jobs

import models._
import org.quartz.{Job, JobExecutionContext}
import play.api.Logger

class SmsAlertsMonthly extends Job {
  override def execute(context: JobExecutionContext) {
    Companies.getAll.filter(c => c.isCableNetwork && c.smsEnabled).foreach({ company =>

        Logger.info(s"Sms Generation started for company:${company.id}")
        try {
          Customers.getAll(company.id.get).foreach({ customer =>
            if (customer._1.balanceAmount > 0 && customer._1.mobileNo.isDefined) {
              val message = s"Dear Customer, You cable connection pending balance is:${customer._1.balanceAmount}. Please pay to our agent to avoid disconnection."
              SmsGateway.sendSms(message, customer._1.mobileNo, company)
            }
          })
        } catch {
          case e: Exception => Logger.info(s"Exception caught in processing bill generation for company:${company.id}", e)
        }
    })
  }
}