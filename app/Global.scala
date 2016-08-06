import jobs.{BalanceUpdaterDailyInternet, SmsAlertsMonthly, BalanceUpdaterMonthlyCable}
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import play.api.{Application, GlobalSettings, Logger}

object Global extends GlobalSettings {

  val scheduler = StdSchedulerFactory.getDefaultScheduler

  override def onStart(app: Application) {
    Logger.info("Quartz scheduler starting...")
    scheduler.start()

    // Balance Update Cron Job
    val balanceJob = newJob(classOf[BalanceUpdaterMonthlyCable]).withIdentity("balanceJob", "balanceGroup").build()

    val balanceTrigger = newTrigger()
      .withIdentity("balanceTrigger", "balanceGroup")
      .withSchedule(cronSchedule("0 20 11 1 * ?")) // Runs on every month 2nd around 1`O Clock
      .build()

    scheduler.scheduleJob(balanceJob, balanceTrigger)

    // Balance Update Cron Job
    val internetBalanceJob = newJob(classOf[BalanceUpdaterDailyInternet]).withIdentity("iBalanceJob", "iBalanceGroup").build()

    val internetBalanceTrigger = newTrigger()
      .withIdentity("iBalanceTrigger", "iBalanceGroup")
      .withSchedule(cronSchedule("0 16 11 * * ?")) // Runs on every month 2nd around 1`O Clock
      .build()

    scheduler.scheduleJob(internetBalanceJob, internetBalanceTrigger)

    // Sms Alerts Cron Job
    val smsJob = newJob(classOf[SmsAlertsMonthly]).withIdentity("smsJob", "smsGroup").build()

    val smsTrigger = newTrigger()
      .withIdentity("smsTrigger", "smsGroup")
      .withSchedule(cronSchedule("0 40 11 1 * ?")) // Runs on every month 2nd around 10`O Clock morning
      .build()

    scheduler.scheduleJob(smsJob, smsTrigger)

  }

  override def onStop(app: Application) {
    Logger.info("Quartz scheduler shutdown.")
    scheduler.shutdown()
  }

}
