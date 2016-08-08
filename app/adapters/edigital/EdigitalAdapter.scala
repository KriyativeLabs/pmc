package adapters.edigital

class EdigitalAdapter(operatorId:String, username:String, password:String) extends AdapterTrait {

  val loginPage = "http://www.edigital.co:91/"

  val homePage = "http://www.edigital.co:91/RstAppForm/xWelcome.aspx"

  override def login: Boolean = ???

  override def searchSTB(stbNo: String): String = ???

  override def deactivateSTB(stbNo: String): Boolean = ???

  override def activateSTB(stbNo: String): Boolean = ???

  override def stbStatus(stbNo: String): Boolean = ???
}
