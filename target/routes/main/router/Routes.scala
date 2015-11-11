
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/surya/paymycable/paymycable/conf/routes
// @DATE:Thu Nov 12 01:33:46 IST 2015

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._

import _root_.controllers.Assets.Asset

object Routes extends Routes

class Routes extends GeneratedRouter {

  import ReverseRouteContext.empty

  override val errorHandler: play.api.http.HttpErrorHandler = play.api.http.LazyHttpErrorHandler

  private var _prefix = "/"

  def withPrefix(prefix: String): Routes = {
    _prefix = prefix
    router.RoutesPrefix.setPrefix(prefix)
    
    this
  }

  def prefix: String = _prefix

  lazy val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation: Seq[(String, String, String)] = List(
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/login""", """controllers.UsersController.login"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users/changepassword""", """controllers.UsersController.updatePassword"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/dashboarddata""", """controllers.DashboardController.dashboardData"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/notifications""", """controllers.DashboardController.notifications"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/sms""", """controllers.SmsController.sendSms"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/agent_stats""", """controllers.DashboardController.agentStatistics"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/company_stats""", """controllers.DashboardController.monthlyStatistics"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/companies""", """controllers.CompaniesController.all"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/companies""", """controllers.CompaniesController.create"""),
    ("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/companies/$id<[^/]+>""", """controllers.CompaniesController.update(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/companies/$id<[^/]+>""", """controllers.CompaniesController.find(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users""", """controllers.UsersController.all"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users""", """controllers.UsersController.create"""),
    ("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users/$id<[^/]+>""", """controllers.UsersController.update(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users/$id<[^/]+>""", """controllers.UsersController.find(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users/$id<[^/]+>/payments""", """controllers.PaymentsController.findByAgentId(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/users/$id<[^/]+>/payments/today""", """controllers.PaymentsController.findByAgentIdToday(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers/unpaid""", """controllers.CustomersController.unpaidCustomers"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers/paid""", """controllers.CustomersController.paidCustomers"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers""", """controllers.CustomersController.all"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers""", """controllers.CustomersController.create"""),
    ("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers/$id<[^/]+>""", """controllers.CustomersController.update(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers/$id<[^/]+>""", """controllers.CustomersController.find(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customersearch""", """controllers.CustomersController.searchCustomers(search:String ?= "")"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/customers/$id<[^/]+>/payments""", """controllers.PaymentsController.findByCustomerId(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/areas""", """controllers.AreasController.all"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/areas""", """controllers.AreasController.create"""),
    ("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/areas/$id<[^/]+>""", """controllers.AreasController.update(id:Int)"""),
    ("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/areas/$id<[^/]+>""", """controllers.AreasController.delete(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/areas/$id<[^/]+>""", """controllers.AreasController.find(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/plans""", """controllers.PlansController.all"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/plans""", """controllers.PlansController.create"""),
    ("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/plans/$id<[^/]+>""", """controllers.PlansController.update(id:Int)"""),
    ("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/plans/$id<[^/]+>""", """controllers.PlansController.delete(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/plans/$id<[^/]+>""", """controllers.PlansController.find(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/payments""", """controllers.PaymentsController.all"""),
    ("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/payments""", """controllers.PaymentsController.create"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/v1/payments/$id<[^/]+>""", """controllers.PaymentsController.find(id:Int)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""", """controllers.Assets.at(path:String = "/public/app/assets/", file:String)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """app/$file<.+>""", """controllers.Assets.at(path:String = "/public/app/", file:String)"""),
    ("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """login""", """controllers.Assets.at(path:String = "/public/app/views", file:String = "login.html")"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_UsersController_login0_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/login")))
  )
  private[this] lazy val controllers_UsersController_login0_invoker = createInvoker(
    controllers.UsersController.login,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.UsersController",
      "login",
      Nil,
      "POST",
      """ login""",
      this.prefix + """api/v1/login"""
    )
  )

  // @LINE:7
  private[this] lazy val controllers_UsersController_updatePassword1_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users/changepassword")))
  )
  private[this] lazy val controllers_UsersController_updatePassword1_invoker = createInvoker(
    controllers.UsersController.updatePassword,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.UsersController",
      "updatePassword",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/users/changepassword"""
    )
  )

  // @LINE:8
  private[this] lazy val controllers_DashboardController_dashboardData2_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/dashboarddata")))
  )
  private[this] lazy val controllers_DashboardController_dashboardData2_invoker = createInvoker(
    controllers.DashboardController.dashboardData,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.DashboardController",
      "dashboardData",
      Nil,
      "GET",
      """""",
      this.prefix + """api/v1/dashboarddata"""
    )
  )

  // @LINE:9
  private[this] lazy val controllers_DashboardController_notifications3_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/notifications")))
  )
  private[this] lazy val controllers_DashboardController_notifications3_invoker = createInvoker(
    controllers.DashboardController.notifications,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.DashboardController",
      "notifications",
      Nil,
      "GET",
      """""",
      this.prefix + """api/v1/notifications"""
    )
  )

  // @LINE:10
  private[this] lazy val controllers_SmsController_sendSms4_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/sms")))
  )
  private[this] lazy val controllers_SmsController_sendSms4_invoker = createInvoker(
    controllers.SmsController.sendSms,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.SmsController",
      "sendSms",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/sms"""
    )
  )

  // @LINE:12
  private[this] lazy val controllers_DashboardController_agentStatistics5_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/agent_stats")))
  )
  private[this] lazy val controllers_DashboardController_agentStatistics5_invoker = createInvoker(
    controllers.DashboardController.agentStatistics,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.DashboardController",
      "agentStatistics",
      Nil,
      "GET",
      """""",
      this.prefix + """api/v1/agent_stats"""
    )
  )

  // @LINE:13
  private[this] lazy val controllers_DashboardController_monthlyStatistics6_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/company_stats")))
  )
  private[this] lazy val controllers_DashboardController_monthlyStatistics6_invoker = createInvoker(
    controllers.DashboardController.monthlyStatistics,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.DashboardController",
      "monthlyStatistics",
      Nil,
      "GET",
      """""",
      this.prefix + """api/v1/company_stats"""
    )
  )

  // @LINE:16
  private[this] lazy val controllers_CompaniesController_all7_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/companies")))
  )
  private[this] lazy val controllers_CompaniesController_all7_invoker = createInvoker(
    controllers.CompaniesController.all,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CompaniesController",
      "all",
      Nil,
      "GET",
      """ Companies""",
      this.prefix + """api/v1/companies"""
    )
  )

  // @LINE:17
  private[this] lazy val controllers_CompaniesController_create8_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/companies")))
  )
  private[this] lazy val controllers_CompaniesController_create8_invoker = createInvoker(
    controllers.CompaniesController.create,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CompaniesController",
      "create",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/companies"""
    )
  )

  // @LINE:18
  private[this] lazy val controllers_CompaniesController_update9_route: Route.ParamsExtractor = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/companies/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_CompaniesController_update9_invoker = createInvoker(
    controllers.CompaniesController.update(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CompaniesController",
      "update",
      Seq(classOf[Int]),
      "PUT",
      """""",
      this.prefix + """api/v1/companies/$id<[^/]+>"""
    )
  )

  // @LINE:20
  private[this] lazy val controllers_CompaniesController_find10_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/companies/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_CompaniesController_find10_invoker = createInvoker(
    controllers.CompaniesController.find(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CompaniesController",
      "find",
      Seq(classOf[Int]),
      "GET",
      """DELETE      /companies/:id                   controllers.CompaniesController.create""",
      this.prefix + """api/v1/companies/$id<[^/]+>"""
    )
  )

  // @LINE:23
  private[this] lazy val controllers_UsersController_all11_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users")))
  )
  private[this] lazy val controllers_UsersController_all11_invoker = createInvoker(
    controllers.UsersController.all,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.UsersController",
      "all",
      Nil,
      "GET",
      """ Users""",
      this.prefix + """api/v1/users"""
    )
  )

  // @LINE:24
  private[this] lazy val controllers_UsersController_create12_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users")))
  )
  private[this] lazy val controllers_UsersController_create12_invoker = createInvoker(
    controllers.UsersController.create,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.UsersController",
      "create",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/users"""
    )
  )

  // @LINE:25
  private[this] lazy val controllers_UsersController_update13_route: Route.ParamsExtractor = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_UsersController_update13_invoker = createInvoker(
    controllers.UsersController.update(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.UsersController",
      "update",
      Seq(classOf[Int]),
      "PUT",
      """""",
      this.prefix + """api/v1/users/$id<[^/]+>"""
    )
  )

  // @LINE:26
  private[this] lazy val controllers_UsersController_find14_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_UsersController_find14_invoker = createInvoker(
    controllers.UsersController.find(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.UsersController",
      "find",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/users/$id<[^/]+>"""
    )
  )

  // @LINE:27
  private[this] lazy val controllers_PaymentsController_findByAgentId15_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users/"), DynamicPart("id", """[^/]+""",true), StaticPart("/payments")))
  )
  private[this] lazy val controllers_PaymentsController_findByAgentId15_invoker = createInvoker(
    controllers.PaymentsController.findByAgentId(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PaymentsController",
      "findByAgentId",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/users/$id<[^/]+>/payments"""
    )
  )

  // @LINE:28
  private[this] lazy val controllers_PaymentsController_findByAgentIdToday16_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/users/"), DynamicPart("id", """[^/]+""",true), StaticPart("/payments/today")))
  )
  private[this] lazy val controllers_PaymentsController_findByAgentIdToday16_invoker = createInvoker(
    controllers.PaymentsController.findByAgentIdToday(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PaymentsController",
      "findByAgentIdToday",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/users/$id<[^/]+>/payments/today"""
    )
  )

  // @LINE:31
  private[this] lazy val controllers_CustomersController_unpaidCustomers17_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers/unpaid")))
  )
  private[this] lazy val controllers_CustomersController_unpaidCustomers17_invoker = createInvoker(
    controllers.CustomersController.unpaidCustomers,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "unpaidCustomers",
      Nil,
      "GET",
      """Customers""",
      this.prefix + """api/v1/customers/unpaid"""
    )
  )

  // @LINE:32
  private[this] lazy val controllers_CustomersController_paidCustomers18_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers/paid")))
  )
  private[this] lazy val controllers_CustomersController_paidCustomers18_invoker = createInvoker(
    controllers.CustomersController.paidCustomers,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "paidCustomers",
      Nil,
      "GET",
      """""",
      this.prefix + """api/v1/customers/paid"""
    )
  )

  // @LINE:33
  private[this] lazy val controllers_CustomersController_all19_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers")))
  )
  private[this] lazy val controllers_CustomersController_all19_invoker = createInvoker(
    controllers.CustomersController.all,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "all",
      Nil,
      "GET",
      """""",
      this.prefix + """api/v1/customers"""
    )
  )

  // @LINE:34
  private[this] lazy val controllers_CustomersController_create20_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers")))
  )
  private[this] lazy val controllers_CustomersController_create20_invoker = createInvoker(
    controllers.CustomersController.create,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "create",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/customers"""
    )
  )

  // @LINE:35
  private[this] lazy val controllers_CustomersController_update21_route: Route.ParamsExtractor = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_CustomersController_update21_invoker = createInvoker(
    controllers.CustomersController.update(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "update",
      Seq(classOf[Int]),
      "PUT",
      """""",
      this.prefix + """api/v1/customers/$id<[^/]+>"""
    )
  )

  // @LINE:36
  private[this] lazy val controllers_CustomersController_find22_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_CustomersController_find22_invoker = createInvoker(
    controllers.CustomersController.find(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "find",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/customers/$id<[^/]+>"""
    )
  )

  // @LINE:37
  private[this] lazy val controllers_CustomersController_searchCustomers23_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customersearch")))
  )
  private[this] lazy val controllers_CustomersController_searchCustomers23_invoker = createInvoker(
    controllers.CustomersController.searchCustomers(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CustomersController",
      "searchCustomers",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """api/v1/customersearch"""
    )
  )

  // @LINE:38
  private[this] lazy val controllers_PaymentsController_findByCustomerId24_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/customers/"), DynamicPart("id", """[^/]+""",true), StaticPart("/payments")))
  )
  private[this] lazy val controllers_PaymentsController_findByCustomerId24_invoker = createInvoker(
    controllers.PaymentsController.findByCustomerId(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PaymentsController",
      "findByCustomerId",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/customers/$id<[^/]+>/payments"""
    )
  )

  // @LINE:41
  private[this] lazy val controllers_AreasController_all25_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/areas")))
  )
  private[this] lazy val controllers_AreasController_all25_invoker = createInvoker(
    controllers.AreasController.all,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AreasController",
      "all",
      Nil,
      "GET",
      """Areas""",
      this.prefix + """api/v1/areas"""
    )
  )

  // @LINE:42
  private[this] lazy val controllers_AreasController_create26_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/areas")))
  )
  private[this] lazy val controllers_AreasController_create26_invoker = createInvoker(
    controllers.AreasController.create,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AreasController",
      "create",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/areas"""
    )
  )

  // @LINE:43
  private[this] lazy val controllers_AreasController_update27_route: Route.ParamsExtractor = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/areas/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_AreasController_update27_invoker = createInvoker(
    controllers.AreasController.update(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AreasController",
      "update",
      Seq(classOf[Int]),
      "PUT",
      """""",
      this.prefix + """api/v1/areas/$id<[^/]+>"""
    )
  )

  // @LINE:44
  private[this] lazy val controllers_AreasController_delete28_route: Route.ParamsExtractor = Route("DELETE",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/areas/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_AreasController_delete28_invoker = createInvoker(
    controllers.AreasController.delete(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AreasController",
      "delete",
      Seq(classOf[Int]),
      "DELETE",
      """""",
      this.prefix + """api/v1/areas/$id<[^/]+>"""
    )
  )

  // @LINE:45
  private[this] lazy val controllers_AreasController_find29_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/areas/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_AreasController_find29_invoker = createInvoker(
    controllers.AreasController.find(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AreasController",
      "find",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/areas/$id<[^/]+>"""
    )
  )

  // @LINE:48
  private[this] lazy val controllers_PlansController_all30_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/plans")))
  )
  private[this] lazy val controllers_PlansController_all30_invoker = createInvoker(
    controllers.PlansController.all,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PlansController",
      "all",
      Nil,
      "GET",
      """Plans""",
      this.prefix + """api/v1/plans"""
    )
  )

  // @LINE:49
  private[this] lazy val controllers_PlansController_create31_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/plans")))
  )
  private[this] lazy val controllers_PlansController_create31_invoker = createInvoker(
    controllers.PlansController.create,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PlansController",
      "create",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/plans"""
    )
  )

  // @LINE:50
  private[this] lazy val controllers_PlansController_update32_route: Route.ParamsExtractor = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/plans/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_PlansController_update32_invoker = createInvoker(
    controllers.PlansController.update(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PlansController",
      "update",
      Seq(classOf[Int]),
      "PUT",
      """""",
      this.prefix + """api/v1/plans/$id<[^/]+>"""
    )
  )

  // @LINE:51
  private[this] lazy val controllers_PlansController_delete33_route: Route.ParamsExtractor = Route("DELETE",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/plans/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_PlansController_delete33_invoker = createInvoker(
    controllers.PlansController.delete(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PlansController",
      "delete",
      Seq(classOf[Int]),
      "DELETE",
      """""",
      this.prefix + """api/v1/plans/$id<[^/]+>"""
    )
  )

  // @LINE:52
  private[this] lazy val controllers_PlansController_find34_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/plans/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_PlansController_find34_invoker = createInvoker(
    controllers.PlansController.find(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PlansController",
      "find",
      Seq(classOf[Int]),
      "GET",
      """""",
      this.prefix + """api/v1/plans/$id<[^/]+>"""
    )
  )

  // @LINE:55
  private[this] lazy val controllers_PaymentsController_all35_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/payments")))
  )
  private[this] lazy val controllers_PaymentsController_all35_invoker = createInvoker(
    controllers.PaymentsController.all,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PaymentsController",
      "all",
      Nil,
      "GET",
      """Payments""",
      this.prefix + """api/v1/payments"""
    )
  )

  // @LINE:56
  private[this] lazy val controllers_PaymentsController_create36_route: Route.ParamsExtractor = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/payments")))
  )
  private[this] lazy val controllers_PaymentsController_create36_invoker = createInvoker(
    controllers.PaymentsController.create,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PaymentsController",
      "create",
      Nil,
      "POST",
      """""",
      this.prefix + """api/v1/payments"""
    )
  )

  // @LINE:58
  private[this] lazy val controllers_PaymentsController_find37_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/v1/payments/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_PaymentsController_find37_invoker = createInvoker(
    controllers.PaymentsController.find(fakeValue[Int]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.PaymentsController",
      "find",
      Seq(classOf[Int]),
      "GET",
      """PUT         /api/v1/plans/:id                controllers.PaymentsController.update(id:Int)""",
      this.prefix + """api/v1/payments/$id<[^/]+>"""
    )
  )

  // @LINE:61
  private[this] lazy val controllers_Assets_at38_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_at38_invoker = createInvoker(
    controllers.Assets.at(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "at",
      Seq(classOf[String], classOf[String]),
      "GET",
      """ Map static resources from the /public folder to the /assets URL path""",
      this.prefix + """assets/$file<.+>"""
    )
  )

  // @LINE:62
  private[this] lazy val controllers_Assets_at39_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("app/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_at39_invoker = createInvoker(
    controllers.Assets.at(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "at",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """app/$file<.+>"""
    )
  )

  // @LINE:63
  private[this] lazy val controllers_Assets_at40_route: Route.ParamsExtractor = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("login")))
  )
  private[this] lazy val controllers_Assets_at40_invoker = createInvoker(
    controllers.Assets.at(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "at",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """login"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_UsersController_login0_route(params) =>
      call { 
        controllers_UsersController_login0_invoker.call(controllers.UsersController.login)
      }
  
    // @LINE:7
    case controllers_UsersController_updatePassword1_route(params) =>
      call { 
        controllers_UsersController_updatePassword1_invoker.call(controllers.UsersController.updatePassword)
      }
  
    // @LINE:8
    case controllers_DashboardController_dashboardData2_route(params) =>
      call { 
        controllers_DashboardController_dashboardData2_invoker.call(controllers.DashboardController.dashboardData)
      }
  
    // @LINE:9
    case controllers_DashboardController_notifications3_route(params) =>
      call { 
        controllers_DashboardController_notifications3_invoker.call(controllers.DashboardController.notifications)
      }
  
    // @LINE:10
    case controllers_SmsController_sendSms4_route(params) =>
      call { 
        controllers_SmsController_sendSms4_invoker.call(controllers.SmsController.sendSms)
      }
  
    // @LINE:12
    case controllers_DashboardController_agentStatistics5_route(params) =>
      call { 
        controllers_DashboardController_agentStatistics5_invoker.call(controllers.DashboardController.agentStatistics)
      }
  
    // @LINE:13
    case controllers_DashboardController_monthlyStatistics6_route(params) =>
      call { 
        controllers_DashboardController_monthlyStatistics6_invoker.call(controllers.DashboardController.monthlyStatistics)
      }
  
    // @LINE:16
    case controllers_CompaniesController_all7_route(params) =>
      call { 
        controllers_CompaniesController_all7_invoker.call(controllers.CompaniesController.all)
      }
  
    // @LINE:17
    case controllers_CompaniesController_create8_route(params) =>
      call { 
        controllers_CompaniesController_create8_invoker.call(controllers.CompaniesController.create)
      }
  
    // @LINE:18
    case controllers_CompaniesController_update9_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_CompaniesController_update9_invoker.call(controllers.CompaniesController.update(id))
      }
  
    // @LINE:20
    case controllers_CompaniesController_find10_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_CompaniesController_find10_invoker.call(controllers.CompaniesController.find(id))
      }
  
    // @LINE:23
    case controllers_UsersController_all11_route(params) =>
      call { 
        controllers_UsersController_all11_invoker.call(controllers.UsersController.all)
      }
  
    // @LINE:24
    case controllers_UsersController_create12_route(params) =>
      call { 
        controllers_UsersController_create12_invoker.call(controllers.UsersController.create)
      }
  
    // @LINE:25
    case controllers_UsersController_update13_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_UsersController_update13_invoker.call(controllers.UsersController.update(id))
      }
  
    // @LINE:26
    case controllers_UsersController_find14_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_UsersController_find14_invoker.call(controllers.UsersController.find(id))
      }
  
    // @LINE:27
    case controllers_PaymentsController_findByAgentId15_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PaymentsController_findByAgentId15_invoker.call(controllers.PaymentsController.findByAgentId(id))
      }
  
    // @LINE:28
    case controllers_PaymentsController_findByAgentIdToday16_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PaymentsController_findByAgentIdToday16_invoker.call(controllers.PaymentsController.findByAgentIdToday(id))
      }
  
    // @LINE:31
    case controllers_CustomersController_unpaidCustomers17_route(params) =>
      call { 
        controllers_CustomersController_unpaidCustomers17_invoker.call(controllers.CustomersController.unpaidCustomers)
      }
  
    // @LINE:32
    case controllers_CustomersController_paidCustomers18_route(params) =>
      call { 
        controllers_CustomersController_paidCustomers18_invoker.call(controllers.CustomersController.paidCustomers)
      }
  
    // @LINE:33
    case controllers_CustomersController_all19_route(params) =>
      call { 
        controllers_CustomersController_all19_invoker.call(controllers.CustomersController.all)
      }
  
    // @LINE:34
    case controllers_CustomersController_create20_route(params) =>
      call { 
        controllers_CustomersController_create20_invoker.call(controllers.CustomersController.create)
      }
  
    // @LINE:35
    case controllers_CustomersController_update21_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_CustomersController_update21_invoker.call(controllers.CustomersController.update(id))
      }
  
    // @LINE:36
    case controllers_CustomersController_find22_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_CustomersController_find22_invoker.call(controllers.CustomersController.find(id))
      }
  
    // @LINE:37
    case controllers_CustomersController_searchCustomers23_route(params) =>
      call(params.fromQuery[String]("search", Some(""))) { (search) =>
        controllers_CustomersController_searchCustomers23_invoker.call(controllers.CustomersController.searchCustomers(search))
      }
  
    // @LINE:38
    case controllers_PaymentsController_findByCustomerId24_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PaymentsController_findByCustomerId24_invoker.call(controllers.PaymentsController.findByCustomerId(id))
      }
  
    // @LINE:41
    case controllers_AreasController_all25_route(params) =>
      call { 
        controllers_AreasController_all25_invoker.call(controllers.AreasController.all)
      }
  
    // @LINE:42
    case controllers_AreasController_create26_route(params) =>
      call { 
        controllers_AreasController_create26_invoker.call(controllers.AreasController.create)
      }
  
    // @LINE:43
    case controllers_AreasController_update27_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_AreasController_update27_invoker.call(controllers.AreasController.update(id))
      }
  
    // @LINE:44
    case controllers_AreasController_delete28_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_AreasController_delete28_invoker.call(controllers.AreasController.delete(id))
      }
  
    // @LINE:45
    case controllers_AreasController_find29_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_AreasController_find29_invoker.call(controllers.AreasController.find(id))
      }
  
    // @LINE:48
    case controllers_PlansController_all30_route(params) =>
      call { 
        controllers_PlansController_all30_invoker.call(controllers.PlansController.all)
      }
  
    // @LINE:49
    case controllers_PlansController_create31_route(params) =>
      call { 
        controllers_PlansController_create31_invoker.call(controllers.PlansController.create)
      }
  
    // @LINE:50
    case controllers_PlansController_update32_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PlansController_update32_invoker.call(controllers.PlansController.update(id))
      }
  
    // @LINE:51
    case controllers_PlansController_delete33_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PlansController_delete33_invoker.call(controllers.PlansController.delete(id))
      }
  
    // @LINE:52
    case controllers_PlansController_find34_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PlansController_find34_invoker.call(controllers.PlansController.find(id))
      }
  
    // @LINE:55
    case controllers_PaymentsController_all35_route(params) =>
      call { 
        controllers_PaymentsController_all35_invoker.call(controllers.PaymentsController.all)
      }
  
    // @LINE:56
    case controllers_PaymentsController_create36_route(params) =>
      call { 
        controllers_PaymentsController_create36_invoker.call(controllers.PaymentsController.create)
      }
  
    // @LINE:58
    case controllers_PaymentsController_find37_route(params) =>
      call(params.fromPath[Int]("id", None)) { (id) =>
        controllers_PaymentsController_find37_invoker.call(controllers.PaymentsController.find(id))
      }
  
    // @LINE:61
    case controllers_Assets_at38_route(params) =>
      call(Param[String]("path", Right("/public/app/assets/")), params.fromPath[String]("file", None)) { (path, file) =>
        controllers_Assets_at38_invoker.call(controllers.Assets.at(path, file))
      }
  
    // @LINE:62
    case controllers_Assets_at39_route(params) =>
      call(Param[String]("path", Right("/public/app/")), params.fromPath[String]("file", None)) { (path, file) =>
        controllers_Assets_at39_invoker.call(controllers.Assets.at(path, file))
      }
  
    // @LINE:63
    case controllers_Assets_at40_route(params) =>
      call(Param[String]("path", Right("/public/app/views")), Param[String]("file", Right("login.html"))) { (path, file) =>
        controllers_Assets_at40_invoker.call(controllers.Assets.at(path, file))
      }
  }
}