import java.io.{FileWriter, BufferedWriter, File}
import java.nio.charset.CodingErrorAction

import controllers.CustomerCreate
import models.{Connection, Customers}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import utils.CSVUtils

import scala.io.{Source, Codec}

object Testing {

  def main(args: Array[String]) = {

    //println(Customers.getAllWithFilters(7, None, None, Some("7829728448"), None,None,None,None).length)
    val formatter = DateTimeFormat.forPattern("dd-MMM-yy")

    val file = new File("/tmp/rejectedfiles")
    val bw = new BufferedWriter(new FileWriter(file))

    var i = 0
    val companyId = 15
    val areaMap = Map(
      "1" -> 160,
      "2" -> 161,
      "3" -> 162,
      "4" -> 163,
      "5" -> 163)

    val planMap = Map(
      "Monthly" -> 84
    )

    var flag = true

    val csvData = new CSVUtils("/Users/surya/Downloads/Mohan_Data_1.csv", false)
    for (values <- csvData.all) {
      if (i != 0 && flag) {
        println(values(10).trim)
        //case class CustomerCreate(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String],
        // address: String, areaId: Int, balanceAmount: Int, connections: List[Connection])
        //case class Connection(id: Option[Int], customerId: Option[Int], setupBoxId: String, boxSerialNo:String, planId: Int, discount: Int, installationDate: DateTime,
        //                     status: String, cafId: String, idProof: String, companyId: Option[Int])
        val cust = CustomerCreate(None,
          values(0),
          (if (values(1).replace(" ", "").length > 0) Some(values(1).replace(" ", "").toLong) else None),
          Some(values(2).trim),
          values(5),
          areaMap.getOrElse(values(4).trim, throw new Exception("Error Parsing")),
          0,
          List(Connection(None, None,
            values(6).trim,
            values(7),
            planMap.getOrElse(values(10).trim, 84),
            0,
            DateTime.now(),
            "ACTIVE",
            "NIL",
            "NIL",
            Some(companyId))))

        println(cust)
        Customers.tempInsert(cust, companyId) match {
          case Right(r) => println(r)
          case Left(l) => println(l); bw.write(values.mkString(",")); bw.newLine();
        }

      }
      i = i + 1
    }
    bw.close()
  }
}
