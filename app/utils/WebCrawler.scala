package utils

import org.scalatest.selenium._
import org.scalatest._
import org.selenium.WebDriver

class WebCrawler extends FlatSpec with Matchers with WebBrowser {
  val URL = "http://www.edigital.co:91"
  val USER_AGENT = "Mozilla/5.0"
  implicit val webDriver: WebDriver = new HtmlUnitDriver
  def goto() = {
      go to  "http://www.amazon.com"
  }

}
