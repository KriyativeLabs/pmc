package adapters.edigital

import adapters.AdapterTrait
import helpers.enums.ConnectionStatus.ConnectionStatus
import org.openqa.selenium.By
import utils.AdapterException

import scala.util.{Failure, Success, Try}

//class EdigitalAdapterV1 extends AdapterTrait {
//
//  val LOGINPAGE = "http://www.edigital.co:91/"
//  val HOMEPAGE = "http://www.edigital.co:91/RstAppForm/xWelcome.aspx"
//  val CAS_XPATH = "//a[@data-caption='CAS']"
//
//  override def resetAndLogin(operatorId: String, username: String, password: String, cookie:String): Boolean = {
//    logout
//    login(operatorId, username, password, cookie)
//  }
//
//  override def login(operatorId: String, username: String, password: String, cookie: String): Boolean = {
//    driver.manage().deleteAllCookies()
//    driver.get(LOGINPAGE)
//
//    //println(driver.manage().getCookieNamed("ASP.NET_SessionId=k2itmnwthrmwocccizhe0wvz").getValue)
//    println(driver.manage().getCookieNamed("ASP.NET_SessionId").getValue)
//
//    val operatorIdElement = driver.findElement(By.id("txtOperatorID"))
//    operatorIdElement.clear()
//    operatorIdElement.sendKeys(operatorId)
//
//    val usernameElement = driver.findElement(By.id("txtLogin"))
//    usernameElement.clear()
//    usernameElement.sendKeys(username)
//
//    val passwordElement = driver.findElement(By.id("txtPassword"))
//    passwordElement.clear()
//    passwordElement.sendKeys(password)
//
//    val SignInButton = driver.findElement(By.id("btnLogin"))
//
//    try {
//      SignInButton.click()
//      handleAlert(true)
//    } catch {
//      case e: Exception =>
//    }
//    casNavigation
//  }
//
//  override def reload: Boolean = {
//    driver.get(HOMEPAGE)
//    casNavigation
//  }
//
//  override def logout: Boolean = {
//
//    Try(driver.findElement(By.id("dm3")).click()) match {
//      case Success(s) => true
//      case Failure(f) => false
//    }
//  }
//
//  override def searchSTB(stbNo: String): String = ???
//
//  override def deactivateSTB(stbNo: String): Boolean = {
////    if (stbStatus(stbNo)) {
////      val deactivate = driver.findElement(By.id("btnActivateDeActivate"))
////      deactivate.click()
////      wait(By.id("Msgokbtn"), 10)
////      val confirm = driver.findElement(By.id("Msgokbtn"))
////      confirm.click()
////      true
////    } else {
////      true
////    }
//    true
//  }
//
//  override def activateSTB(stbNo: String): Boolean = {
////    if (!stbStatus(stbNo)) {
////      val activate = driver.findElement(By.id("btnActivateDeActivate"))
////      activate.click()
////      wait(By.id("Msgokbtn"), 10)
////      val confirm = driver.findElement(By.id("Msgokbtn"))
////      confirm.click()
//      wait(By.xpath("//div[@class="div"]"))
////      true
////    } else {
////      true
////    }
//    true
//  }
//
//  override def stbStatus(stbNo: String): ConnectionStatus = ???
////  Boolean = {
////    wait(By.id("txtCardNo"), 20)
////    val cardElement = driver.findElement(By.id("txtCardNo"))
////    cardElement.clear()
////    cardElement.sendKeys(stbNo)
////    driver.findElement(By.id("Span1")).click()
////    Try(wait(By.id("Status"), 10)) match {
////      case Success(s) => driver.findElement(By.id("Status")).getText.toLowerCase match {
////        case s if s.contains("running") || s.contains("active") => true
////        case _ => false
////      }
////      case Failure(s) => throw AdapterException("Cannot determine status of the sbt no, either its invalid")
////    }
////  }
//
//  private def casNavigation: Boolean = {
//    Try(wait(By.xpath(CAS_XPATH), 30)) match {
//      case Success(s) => {
//        driver.findElement(By.xpath(CAS_XPATH)).click()
//        true
//      }
//      case Failure(f) => logger.error("Not able to navigate to cas page", f); false
//    }
//  }
//
//
//}
