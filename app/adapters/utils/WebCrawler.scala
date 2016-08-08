package adapters.utils

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

object WebCrawler {

  def main (args: Array[String]) {

    val driver = new FirefoxDriver()
    driver.manage().deleteAllCookies()

    val appUrl = "http://www.edigital.co:91/"
//    val appUrl = "http://www.edigital.co:91/RstAppForm/xWelcome.aspx"
    // launch the firefox browser and open the application url
    driver.get(appUrl)
    // maximize the browser window
   driver.manage().window().maximize()
    // declare and initialize the variable to store the expected title of the webpage.
    // enter a valid username in the email textbox
    val operatorId = driver.findElement(By.id("txtOperatorID"))
    operatorId.clear()
    operatorId.sendKeys("Eieipl")
    // enter a valid password in the password textbox
    val username = driver.findElement(By.id("txtLogin"))
    username.clear()
    username.sendKeys("Lcotip001")
    val password = driver.findElement(By.id("txtPassword"))
    password.clear()
    password.sendKeys("V2digital")

    // click on the Sign in button
    val SignInButton = driver.findElement(By.id("btnLogin"))
    SignInButton.click()

    val wait = new WebDriverWait(driver, 30)
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@data-caption='CAS']")))
    val casButton = driver.findElementByXPath("//a[@data-caption='CAS']")
    casButton.click()
    //driver.get("http://www.edigital.co:91/RstAppForm/ActivateConn.aspx")
    // close the web browser
    driver.quit()
    Thread.sleep(50000)

    // terminate the program
    //System.exit(0)
  }

}
