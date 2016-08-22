package utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.matching.Regex

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

  def extractMatch(regex: Regex, input: String): Option[String] = {
    for (regex(group) <- regex.findFirstIn(input)) yield group
  }
}

class StringMethods(s: String) {

  def substitute[T](valueMap: Map[String, T]): String = {
    var result = s
    valueMap.foreach(x => {
      result = result.replace("${" + x._1.toUpperCase + "}", x._2.toString)
    })
    result
  }

}

