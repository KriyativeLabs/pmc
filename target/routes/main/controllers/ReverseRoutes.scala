
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/surya/paymycable/paymycable/conf/routes
// @DATE:Thu Nov 12 02:44:46 IST 2015

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset

// @LINE:6
package controllers {

  // @LINE:62
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:62
    def at(path:String, file:String): Call = {
    
      (path: @unchecked, file: @unchecked) match {
      
        // @LINE:62
        case (path, file) if path == "/public/app/assets/" =>
          implicit val _rrc = new ReverseRouteContext(Map(("path", "/public/app/assets/")))
          Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
      
        // @LINE:63
        case (path, file) if path == "/public/app/" =>
          implicit val _rrc = new ReverseRouteContext(Map(("path", "/public/app/")))
          Call("GET", _prefix + { _defaultPrefix } + "app/" + implicitly[PathBindable[String]].unbind("file", file))
      
        // @LINE:64
        case (path, file) if path == "/public/app/views" && file == "login.html" =>
          implicit val _rrc = new ReverseRouteContext(Map(("path", "/public/app/views"), ("file", "login.html")))
          Call("GET", _prefix + { _defaultPrefix } + "login")
      
      }
    
    }
  
  }

  // @LINE:27
  class ReversePaymentsController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:27
    def findByAgentId(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/users/" + implicitly[PathBindable[Int]].unbind("id", id) + "/payments")
    }
  
    // @LINE:38
    def findByCustomerId(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/customers/" + implicitly[PathBindable[Int]].unbind("id", id) + "/payments")
    }
  
    // @LINE:57
    def create(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/payments")
    }
  
    // @LINE:59
    def find(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/payments/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:55
    def all(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/payments")
    }
  
    // @LINE:56
    def search(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/payments/advanced")
    }
  
    // @LINE:28
    def findByAgentIdToday(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/users/" + implicitly[PathBindable[Int]].unbind("id", id) + "/payments/today")
    }
  
  }

  // @LINE:41
  class ReverseAreasController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:43
    def update(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("PUT", _prefix + { _defaultPrefix } + "api/v1/areas/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:42
    def create(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/areas")
    }
  
    // @LINE:44
    def delete(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("DELETE", _prefix + { _defaultPrefix } + "api/v1/areas/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:45
    def find(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/areas/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:41
    def all(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/areas")
    }
  
  }

  // @LINE:31
  class ReverseCustomersController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:35
    def update(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("PUT", _prefix + { _defaultPrefix } + "api/v1/customers/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:34
    def create(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/customers")
    }
  
    // @LINE:36
    def find(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/customers/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:32
    def paidCustomers(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/customers/paid")
    }
  
    // @LINE:33
    def all(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/customers")
    }
  
    // @LINE:37
    def searchCustomers(search:String = ""): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/customersearch" + queryString(List(if(search == "") None else Some(implicitly[QueryStringBindable[String]].unbind("search", search)))))
    }
  
    // @LINE:31
    def unpaidCustomers(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/customers/unpaid")
    }
  
  }

  // @LINE:48
  class ReversePlansController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:50
    def update(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("PUT", _prefix + { _defaultPrefix } + "api/v1/plans/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:49
    def create(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/plans")
    }
  
    // @LINE:51
    def delete(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("DELETE", _prefix + { _defaultPrefix } + "api/v1/plans/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:52
    def find(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/plans/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:48
    def all(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/plans")
    }
  
  }

  // @LINE:8
  class ReverseDashboardController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def notifications(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/notifications")
    }
  
    // @LINE:8
    def dashboardData(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/dashboarddata")
    }
  
    // @LINE:12
    def agentStatistics(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/agent_stats")
    }
  
    // @LINE:13
    def monthlyStatistics(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/company_stats")
    }
  
  }

  // @LINE:16
  class ReverseCompaniesController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:20
    def find(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/companies/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:16
    def all(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/companies")
    }
  
    // @LINE:17
    def create(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/companies")
    }
  
    // @LINE:18
    def update(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("PUT", _prefix + { _defaultPrefix } + "api/v1/companies/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
  }

  // @LINE:10
  class ReverseSmsController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:10
    def sendSms(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/sms")
    }
  
  }

  // @LINE:6
  class ReverseUsersController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:25
    def update(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("PUT", _prefix + { _defaultPrefix } + "api/v1/users/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:24
    def create(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/users")
    }
  
    // @LINE:26
    def find(id:Int): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/users/" + implicitly[PathBindable[Int]].unbind("id", id))
    }
  
    // @LINE:23
    def all(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api/v1/users")
    }
  
    // @LINE:7
    def updatePassword(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/users/changepassword")
    }
  
    // @LINE:6
    def login(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "api/v1/login")
    }
  
  }


}