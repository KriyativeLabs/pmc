import java.io.{FileWriter, BufferedWriter, File}
import java.nio.charset.CodingErrorAction

import controllers.CustomerCreate
import controllers.CustomersController._
import models.{Customers, Connection}
import org.joda.time.DateTime

/**
 * Created by suryap on 27/10/15.
 */
import scala.io.Source
import scala.io.Codec
object Testing {

  def main(args:Array[String]) = {

    println(Customers.getAllWithFilters(7, None, None, Some("7829728448"), None,None,None,None).length)


    /*
    val file = new File("/tmp/rejectedfiles")
    val bw = new BufferedWriter(new FileWriter(file))


    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    var i = 0
    val areaMap = Map(
      "BG" -> 64,
      "CH" -> 65)

    var flag = false
    for(line <- Source.fromFile("/Users/surya/Downloads/Excel_Header_bagalur.csv")(decoder).getLines()){

      val values = line.split(",")

      if(values(8) == "1766811139"){
        flag = true
      }

      if(i!=0 && flag) {

        //case class CustomerCreate(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String],
        // address: String, areaId: Int, balanceAmount: Int, connections: List[Connection])
        //case class Connection(id: Option[Int], customerId: Option[Int], setupBoxId: String, boxSerialNo:String, planId: Int, discount: Int, installationDate: DateTime,
        //                     status: String, cafId: String, idProof: String, companyId: Option[Int])
        val cust = CustomerCreate(None, values(0), (if (values(1).replace(" ", "").length > 0 ) Some(values(1).replace(" ", "").toLong) else None), None, values(6), if(values(4)=="1") 64 else 65, 0,
          List(Connection(None, None, values(6), values(8), 39, 0, DateTime.now().withDayOfMonth(1), "ACTIVE", values(7), values(12), Some(7))))

          println(cust)
//        println(values(5))

        Customers.tempInsert(cust, 7) match {
          case Right(r) => println(r)
          case Left(l) => println(l);bw.write(line); bw.newLine();
        }
      }
      i = i+1
    }
    bw.close()
    */
  }
}
