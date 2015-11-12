package utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object CommonUtils {
  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

  def string2Date(dateString: String): Option[DateTime] = {
    try {
      Some(dateFormat.parseDateTime(dateString).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0))
    } catch {
      case e: Exception => None
    }
  }

  def yesterday = DateTime.now().minusDays(1).withTime(23, 59, 59, 999)
}
