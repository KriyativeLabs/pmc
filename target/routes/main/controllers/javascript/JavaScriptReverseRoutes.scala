
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/surya/paymycable/paymycable/conf/routes
// @DATE:Sun Nov 08 01:52:31 IST 2015

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset

// @LINE:6
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:57
  class ReverseAssets(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:57
    def at: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Assets.at",
      """
        function(path,file) {
        
          if (path == """ + implicitly[JavascriptLiteral[String]].to("/public/app/assets/") + """) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
          }
        
          if (path == """ + implicitly[JavascriptLiteral[String]].to("/public/app/") + """) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "app/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
          }
        
          if (path == """ + implicitly[JavascriptLiteral[String]].to("/public/app/views") + """ && file == """ + implicitly[JavascriptLiteral[String]].to("login.html") + """) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "login"})
          }
        
        }
      """
    )
  
  }

  // @LINE:51
  class ReversePaymentsController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:54
    def find: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PaymentsController.find",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/payments/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:51
    def all: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PaymentsController.all",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/payments"})
        }
      """
    )
  
    // @LINE:52
    def create: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PaymentsController.create",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/payments"})
        }
      """
    )
  
  }

  // @LINE:37
  class ReverseAreasController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:39
    def update: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AreasController.update",
      """
        function(id) {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/areas/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:38
    def create: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AreasController.create",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/areas"})
        }
      """
    )
  
    // @LINE:40
    def delete: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AreasController.delete",
      """
        function(id) {
          return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/areas/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:41
    def find: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AreasController.find",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/areas/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:37
    def all: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AreasController.all",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/areas"})
        }
      """
    )
  
  }

  // @LINE:28
  class ReverseCustomersController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:32
    def update: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.update",
      """
        function(id) {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customers/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:31
    def create: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.create",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customers"})
        }
      """
    )
  
    // @LINE:33
    def find: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.find",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customers/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:29
    def paidCustomers: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.paidCustomers",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customers/paid"})
        }
      """
    )
  
    // @LINE:30
    def all: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.all",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customers"})
        }
      """
    )
  
    // @LINE:34
    def searchCustomers: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.searchCustomers",
      """
        function(search) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customersearch" + _qS([(search == null ? null : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("search", search))])})
        }
      """
    )
  
    // @LINE:28
    def unpaidCustomers: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CustomersController.unpaidCustomers",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/customers/unpaid"})
        }
      """
    )
  
  }

  // @LINE:44
  class ReversePlansController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:46
    def update: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PlansController.update",
      """
        function(id) {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/plans/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:45
    def create: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PlansController.create",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/plans"})
        }
      """
    )
  
    // @LINE:47
    def delete: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PlansController.delete",
      """
        function(id) {
          return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/plans/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:48
    def find: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PlansController.find",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/plans/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:44
    def all: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.PlansController.all",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/plans"})
        }
      """
    )
  
  }

  // @LINE:8
  class ReverseDashboardController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def notifications: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.DashboardController.notifications",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/notifications"})
        }
      """
    )
  
    // @LINE:8
    def dashboardData: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.DashboardController.dashboardData",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/dashboarddata"})
        }
      """
    )
  
    // @LINE:12
    def agentStatistics: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.DashboardController.agentStatistics",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/agent_stats"})
        }
      """
    )
  
    // @LINE:13
    def monthlyStatistics: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.DashboardController.monthlyStatistics",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/company_stats"})
        }
      """
    )
  
  }

  // @LINE:16
  class ReverseCompaniesController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:20
    def find: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CompaniesController.find",
      """
        function(id) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/companies/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:16
    def all: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CompaniesController.all",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/companies"})
        }
      """
    )
  
    // @LINE:17
    def create: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CompaniesController.create",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/companies"})
        }
      """
    )
  
    // @LINE:18
    def update: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CompaniesController.update",
      """
        function(id) {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/companies/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
  }

  // @LINE:10
  class ReverseSmsController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:10
    def sendSms: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.SmsController.sendSms",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/sms"})
        }
      """
    )
  
  }

  // @LINE:6
  class ReverseUsersController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:25
    def update: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.UsersController.update",
      """
        function(id) {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/users/" + (""" + implicitly[PathBindable[Int]].javascriptUnbind + """)("id", id)})
        }
      """
    )
  
    // @LINE:24
    def create: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.UsersController.create",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/users"})
        }
      """
    )
  
    // @LINE:23
    def all: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.UsersController.all",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/users"})
        }
      """
    )
  
    // @LINE:7
    def updatePassword: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.UsersController.updatePassword",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/users/changepassword"})
        }
      """
    )
  
    // @LINE:6
    def login: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.UsersController.login",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/v1/login"})
        }
      """
    )
  
  }


}