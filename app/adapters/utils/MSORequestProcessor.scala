package adapters.utils

import adapters.AdapterTrait
import adapters.edigital.EdigitalAdapter
import helpers.enums.MSOType.MSOType
import helpers.enums.ResponseStatus.ResponseStatus
import helpers.enums.{MSOType, ResponseStatus}
import org.joda.time.DateTime
import scala.collection.mutable.{HashMap, Queue}

object MSORequestProcessor {

  private val adapters = new HashMap[Int, AdapterTrait]()

  def submit(request: MSORequest, timeout: Int = 30): MSOResponse = {
    processRequest(request)
  }

  private def adapter(request: MSORequest): Option[AdapterTrait] = {
    adapters.get(request.companyId) match {
      case Some(adap) => Some(adap)
      case None => {
        request.msoType match {
          case MSOType.EDIGITAL => {
            Some(new EdigitalAdapter(request.cred1, request.cred2, request.cred3, request.cred4))
          }
          case MSOType.UNKNOWN => None
        }
      }
    }
  }

  private def processRequest(request: MSORequest): MSOResponse = {
    adapter(request) match {
      case Some(adap) => {
        try {
          if (request.activate) {
            adap.login
            adap.activateSTB(request.stbNo.trim)
          } else {
            adap.login
            adap.deactivateSTB(request.stbNo.trim)
          }
          MSOResponse(ResponseStatus.SUCCESS)
        } catch {
          case e: Throwable => MSOResponse(ResponseStatus.ERROR, Some(e.getMessage))
        }
      }
      case None => {
        MSOResponse(ResponseStatus.ERROR, Some(s"MSO ${request.msoType} not supported yet"))
      }
    }
  }
}

//object MSORequestProcessor {
//
//  private val requests = new Queue[MSORequest]()
//
//  private var results = new HashMap[Int, MSOResponse]()
//
//  private var presentRequest: Option[MSORequest] = None
//
//  private val adapters = new HashMap[Int, AdapterTrait]()
//
//  private var isRunning = false
//
//  def submit(request: MSORequest, timeout: Int = 30): MSOResponse = {
//    requests.enqueue(request)
//    if (!isRunning) {
//      run()
//    }
//    val expiryTime = DateTime.now().plusSeconds(timeout)
//    while (expiryTime.isAfterNow && !results.contains(request.connectionId)) {
//      Thread.sleep(5000)
//    }
//
//    if (results.contains(request.connectionId)) {
//      result(request.connectionId).get
//    } else {
//      MSOResponse(ResponseStatus.ERROR, Some("Timeout processing request"))
//    }
//  }
//
//  def checkStatus(connectionId: Int): Boolean = {
//    results.contains(connectionId)
//  }
//
//  def result(connectionId: Int): Option[MSOResponse] = {
//    results.get(connectionId) match {
//      case Some(result) => {
//        results -= connectionId
//        Some(result)
//      }
//      case None => None
//    }
//  }
//
//  def run() {
//    isRunning = true
//    while (requests.nonEmpty) {
//      processRequest(requests.dequeue())
//    }
//    isRunning = false
//  }
//
//  private def adapter(request: MSORequest): Option[AdapterTrait] = {
//    adapters.get(request.companyId) match {
//      case Some(adap) => Some(adap)
//      case None => {
//        request.msoType match {
//          case MSOType.EDIGITAL => {
//            Some(new EdigitalAdapter(request.cred1, request.cred2, request.cred3, request.cred4))
//          }
//          case MSOType.UNKNOWN => None
//        }
//      }
//    }
//  }
//
//  private def processRequest(request: MSORequest) = {
//    adapter(request) match {
//      case Some(adap) => {
//        try {
//          if (request.activate) {
//            adap.activateSTB(request.stbNo.trim)
//          } else {
//            adap.deactivateSTB(request.stbNo.trim)
//          }
//          results.+((request.connectionId, MSOResponse(ResponseStatus.SUCCESS)))
//        } catch {
//          case e: Throwable => results.+((request.connectionId, MSOResponse(ResponseStatus.ERROR, Some(e.getMessage))))
//        }
//      }
//      case None => {
//        results.+((request.connectionId, MSOResponse(ResponseStatus.ERROR, Some(s"MSO ${request.msoType} not supported yet"))))
//      }
//    }
//  }
//}


case class MSORequest(connectionId: Int, companyId: Int, stbNo: String, activate: Boolean, msoType: MSOType, cred1: String, cred2: String, cred3: String, cred4: String)

case class MSOResponse(status: ResponseStatus, errorMessage: Option[String] = None)

