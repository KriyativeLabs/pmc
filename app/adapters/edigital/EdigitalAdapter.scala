package adapters.edigital

import adapters.AdapterTrait
import helpers.enums.ConnectionStatus
import helpers.enums.ConnectionStatus.ConnectionStatus
import utils.CommonUtils
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import dispatch.Http
import dispatch.as
import dispatch.{url => dispatchUrl}
import scala.concurrent.ExecutionContext.Implicits.global

class EdigitalAdapter(operatorId: String, username: String, password: String, cookie: String) extends AdapterTrait {

  val LOGINPAGE = ""
  val HOMEPAGE = "http://www.edigital.co:91/RstAppForm/xWelcome.aspx"
  val CAS_XPATH = "//a[@data-caption='CAS']"

  val LOGIN_PAGE = "http://www.edigital.co:91/Default.aspx/LoginCheck"
  val LOGIN_LOGOUT_PAGE = "http://www.edigital.co:91/GlobalService.svc/LogInLogoutUser"

  val SEARCH_SBT_PAGE = "http://www.edigital.co:91/RstAppForm/ActivateConn.aspx/SearchRecord"
  val SEARCH_REGEX = """.*\\\"ConnStatus\\\":\\\"([a-zA-Z0-9\/]+)\\\".*""".r
  val SEARCH_DATE = """.*,\\\"SubsModifyTimeStamp\\\":\\\"([a-zA-Z0-9-: ]+)\\\",.*""".r
  val SEARCH_CONNID = """\\\"ConnID\\\":([0-9]+),.*""".r
  val SEARCH_CUSTOMER_ID = """\\\"CustomerID\\\":([0-9]+),.*""".r

  val DEACTIVATE_PAGE = "http://www.edigital.co:91/RstAppForm/ActivateConn.aspx/DeactivateSmartCard"
  val ACTIVATE_PAGE = "http://www.edigital.co:91/RstAppForm/ActivateConn.aspx/ActivateSmartCard"
  var timeStamp = ""

  override def resetAndLogin: Boolean = {
    logout
    login
  }

  override def login: Boolean = {
    val result = submitRequest(LOGIN_PAGE, s"{'OperatorID': '${operatorId}','UserID': '${username}','Password': '${password}'}")
    if (result.contains("login_logout_function")) {
      submitRequest(LOGIN_LOGOUT_PAGE, """{"LogStatus": "OUTIN"}""")
      true
    } else if (result.contains("error")) {
      logger.error(result)
      false
    } else {
      true
    }
  }

  override def reload: Boolean = ???

  override def logout: Boolean = {
    true
  }

  override def searchSTB(stbNo: String): String = ???

  override def deactivateSTB(stbNo: String): Boolean = {
    val status = stbStatus(stbNo)
    status._1 match {
      case ConnectionStatus.ACTIVE => {
        println("{'CustomerID': ${CUSTOMER_ID},'SmartCard': '${STB_NO}','ConnId': ${CON_ID},'ModifyTimeStamp': '${M_DATE}'}".substitute(status._2 ++ Map("STB_NO" -> stbNo)))
        val result = submitRequest(DEACTIVATE_PAGE, "{'CustomerID': ${CUSTOMER_ID},'SmartCard': '${STB_NO}','ConnId': ${CON_ID},'ModifyTimeStamp': '${M_DATE}'}".substitute(status._2 ++ Map("STB_NO" -> stbNo)))
        logger.info(result)
        result.contains("Successfully")
      }
      case ConnectionStatus.IN_ACTIVE => true
      case ConnectionStatus.UNKNOWN => false
    }
  }

  override def activateSTB(stbNo: String): Boolean = {
    val status = stbStatus(stbNo)
    status._1 match {
      case ConnectionStatus.ACTIVE => true
      case ConnectionStatus.IN_ACTIVE => {
        val result = submitRequest(ACTIVATE_PAGE, "{'CustomerID': ${CUSTOMER_ID},'SmartCard': '${STB_NO}','ConnId': ${CON_ID},'ModifyTimeStamp': '${M_DATE}'}".substitute(status._2 ++ Map("STB_NO" -> stbNo)))
        result.contains("Successfully")
      }
      case ConnectionStatus.UNKNOWN => false
    }
  }

  override def stbStatus(stbNo: String): (ConnectionStatus, Map[String, String]) = {
    val result = submitRequest(SEARCH_SBT_PAGE, s"{'sCriStr': '${stbNo}',type:'VC'}")
    logger.info(result)
    CommonUtils.extractMatch(SEARCH_REGEX, result) match {
      case Some(x) if x.contains("Active/Running") => (ConnectionStatus.ACTIVE, extractParams(result))
      case Some(x) => (ConnectionStatus.IN_ACTIVE, extractParams(result))
      case None => (ConnectionStatus.UNKNOWN, Map())
    }
  }

  private def extractParams(json: String): Map[String, String] = {
    Map("M_DATE" -> CommonUtils.extractMatch(SEARCH_DATE, json),
      "CUSTOMER_ID" -> CommonUtils.extractMatch(SEARCH_CUSTOMER_ID, json),
      "CON_ID" -> CommonUtils.extractMatch(SEARCH_CONNID, json)).map(m => m._1 -> m._2.getOrElse("").trim)
  }

  private def submitRequest(page: String, data: String): String = {
    logger.info("Processing:" + page)
    val request = dispatchUrl(page).POST.
      //      addCookie(cookie).
      addHeader("Accept", "application/json").
      addHeader("Accept-Encoding", "gzip").
      addHeader("Accept-Language", "en-US").
      addHeader("Connection", "keep-alive").
      addHeader("Content-Type", "application/json; charset=UTF-8").
      addHeader("Cookie", s"ASP.NET_SessionId=${cookie}").
      addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36").
      addHeader("X-Requested-With", "XMLHttpRequest").
      addHeader("Content-Length", s"${data.length}").
      setBody(data)
    Await.result(Http(request OK as.String), Duration(20, SECONDS))
  }


}