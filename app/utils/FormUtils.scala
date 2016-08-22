package utils

import org.joda.time.DateTime
import play.api.data.format.{Formats, Formatter}
import play.api.data.{FormError, Forms, Mapping}
import play.api.libs.json.Reads

object FormUtils {

  def enum[E <: Enumeration](enum: E): Mapping[E#Value] = Forms.of(enumFormat(enum))

  private def enumFormat[E <: Enumeration](enum: E): Formatter[E#Value] = new Formatter[E#Value] {
    def bind(key: String, data: Map[String, String]) = {
      play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[E#Value]
          .either(enum.withName(s.toUpperCase))
          .left.map(e => Seq(FormError(key, s"Should be one of ${enumToString(enum)}", Nil)))
      }
    }

    def unbind(key: String, value: E#Value) = Map(key -> value.toString)
  }

  private def enumToString[E <: Enumeration](enum: E): String = {
    enum.values.map{_.toString}.toString.replace("TreeSet(", "").replace(")", "")
  }



  def jodaDateTime: Mapping[DateTime] = Forms.of(_jodaDateTime("yyyy-MM-dd HH:mm:ss.SSS"))

  private def _jodaDateTime(pattern: String, timeZone: org.joda.time.DateTimeZone = org.joda.time.DateTimeZone.forID("IST")): Formatter[org.joda.time.DateTime] = new Formatter[org.joda.time.DateTime] {

    val formatter = org.joda.time.format.DateTimeFormat.forPattern(pattern).withZone(timeZone)

    override val format = Some(("format.date", Seq(pattern)))

    def bind(key: String, data: Map[String, String]) = {
      var input = data.get(key).get
      if (input.length == 10) {
        // format = yyyy-MM-dd
        input = s"$input 00:00:00.000"
      } else if (input.length == 16) {
        // format = yyyy-MM-dd HH:mm
        input = s"${input.replace('T', ' ').substring(0, 16)}:00.000"
      } else if (input.length == 19) {
        // format = yyyy-MM-dd HH:mm:ss
        input = s"${input.replace('T', ' ').substring(0, 19)}.000"
      } else if (input.length > 19) {
        // format = yyyy-MM-dd'T'HH:mm:ss.SSS[zZ]
        input = input.replace('T', ' ').substring(0, 23)
      } else {
        input = input
      }
      val value = (key, input)
      parsing(formatter.parseDateTime, "error.date", Nil)(key, (data + value))
    }

    def unbind(key: String, value: org.joda.time.DateTime) = Map(key -> value.withZone(timeZone).toString(pattern))

    private def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[T]
          .either(parse(s))
          .left.map(e => Seq(FormError(key, errMsg, errArgs)))
      }
    }
  }
}
