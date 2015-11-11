
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/surya/paymycable/paymycable/conf/routes
// @DATE:Thu Nov 12 02:44:46 IST 2015


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
