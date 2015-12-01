import java.util.Date

import jobs.BalanceUpdaterMonthly
import org.quartz.JobBuilder.newJob
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Job
import org.quartz.JobExecutionContext

import play.api.Application
import play.api.GlobalSettings
import play.api.Logger

object Global extends GlobalSettings {

  val scheduler = StdSchedulerFactory.getDefaultScheduler

  override def onStart(app: Application) {
    Logger.info("Quartz scheduler starting...")
    scheduler.start()

    // Balance Update Cron Job
    val balanceJob = newJob(classOf[BalanceUpdaterMonthly]).withIdentity("balanceJob", "balanceGroup").build()

    val balanceTrigger = newTrigger()
      .withIdentity("balanceTrigger", "balanceGroup")
      .withSchedule(cronSchedule("0 0 1 2 * ?")) // Runs on every month 2nd around 1`O Clock
      .build()

    scheduler.scheduleJob(balanceJob, balanceTrigger)

    // Sms Alerts Cron Job
    val smsJob = newJob(classOf[BalanceUpdaterMonthly]).withIdentity("smsJob", "smsGroup").build()

    val smsTrigger = newTrigger()
      .withIdentity("smsTrigger", "smsGroup")
      .withSchedule(cronSchedule("0 0 10 2 * ?")) // Runs on every month 2nd around 10`O Clock morning
      .build()

    scheduler.scheduleJob(smsJob, smsTrigger)

  }

  override def onStop(app: Application) {
    Logger.info("Quartz scheduler shutdown.")
    scheduler.shutdown()
  }

}