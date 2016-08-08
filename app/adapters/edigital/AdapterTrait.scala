package adapters.edigital

trait AdapterTrait {

  val loginPage:String

  def login: Boolean

  def searchSTB(stbNo: String): String

  def stbStatus(stbNo: String): Boolean

  def activateSTB(stbNo: String): Boolean

  def deactivateSTB(stbNo: String): Boolean

}
