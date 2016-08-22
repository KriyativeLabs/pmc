package utils

import helpers.enums._
import play.api.libs.json._
import slick.driver.PostgresDriver.api._

object EnumTypeHelper {
  def enum2StringMapper(enum: Enumeration) = MappedColumnType.base[enum.Value, String](
    b => b.toString,
    s => enum.withName(s))
}

trait EnumImplicits {

  implicit val msoTypeIm = EnumTypeHelper.enum2StringMapper(MSOType)
  implicit val userType = EnumTypeHelper.enum2StringMapper(UserType)
  implicit val connectionStatus = EnumTypeHelper.enum2StringMapper(ConnectionStatus)
}


object EnumUtils {
  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
    def reads(json: JsValue): JsResult[E#Value] = json match {
      case JsString(s) => {
        try {
          JsSuccess(enum.withName(s.toUpperCase))
        } catch {
          case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(v: E#Value): JsValue = JsString(v.toString)
  }

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(EnumUtils.enumReads(enum), EnumUtils.enumWrites)
  }
}