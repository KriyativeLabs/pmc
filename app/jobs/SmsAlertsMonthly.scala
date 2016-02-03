package jobs

import models._
import org.quartz.{Job, JobExecutionContext}
import play.api.Logger

class SmsAlertsMonthly extends Job {
  override def execute(context: JobExecutionContext) {
    Companies.getAll.foreach({ company =>
      if (company.smsEnabled) {
        Logger.info(s"Sms Generation started for company:${company.id}")
        try {
          Customers.getAll(company.id.get).foreach({ customer =>
            if (customer.customer.balanceAmount > 0 && customer.customer.mobileNo.isDefined) {
              val message = s"Dear Customer, You cable connection pending balance is:${customer.customer.balanceAmount}. Please pay to our agent to avoid disconnection."
              SmsGateway.sendSms(message, customer.customer.mobileNo)
            }
          })
        } catch {
          case e: Exception => Logger.info(s"Exception caught in processing bill generation for company:${company.id}", e)
        }
      }
    })
  }
}