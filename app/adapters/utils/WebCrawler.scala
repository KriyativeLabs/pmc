package adapters.utils

import adapters.edigital.EdigitalAdapter

import scala.io.Source

object WebCrawler {

  def main (args: Array[String]) {

    Source.fromURL("http://google.com")
//    val ed = new EdigitalAdapter("Eieipl", "Lcotip001", "V2digital")
//    if(ed.login){
//      println(ed.deactivateSTB("12001ee821"))
//      println(ed.activateSTB("12001ee821"))

//      println(ed.stbStatus("12001ee821"))
//      ed.reload
//      println(ed.stbStatus("12001ee821"))
//    }

//    Thread.sleep(200000)
//    ed.clean()

    // terminate the program
    //System.exit(0)
  }

}
