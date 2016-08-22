package adapters

import _root_.utils.StringMethods
import com.gargoylesoftware.htmlunit.BrowserVersion
import helpers.enums.ConnectionStatus.ConnectionStatus
import org.openqa.selenium.By
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.Logger

trait AdapterTrait {

  implicit def StringUtil(s: String) = new StringMethods(s)
  val logger = Logger(this.getClass)

  val driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_45)
  driver.setJavascriptEnabled(true)

  def login: Boolean

  def resetAndLogin: Boolean

  def logout: Boolean

  def reload: Boolean

  def searchSTB(stbNo: String): String

  def stbStatus(stbNo: String): (ConnectionStatus, Map[String, String])

  def activateSTB(stbNo: String): Boolean

  def deactivateSTB(stbNo: String): Boolean

  def clean(): Unit = {
  }

  override def finalize(): Unit = {
    clean()
  }

}
