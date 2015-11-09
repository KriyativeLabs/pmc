import org.joda.time.DateTime

/**
 * Created by suryap on 27/10/15.
 */
object Testing {

  def main(args:Array[String]) ={
    println(DateTime.now().minusMonths(1).dayOfMonth().withMaximumValue().withTime(23,59,59,999))
  }
}
