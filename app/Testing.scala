import java.io.{FileWriter, BufferedWriter, File}
import helpers.enums.ConnectionStatus
import models.{CustomerCapsule, Connection, Customers}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import utils.CSVUtils
import scala.util._
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
      "BVR" -> 160,
      "CL" -> 161,
      "KD" -> 162,
      "RN" -> 163,
      "NH" -> 161,
      "GC"->165,
      "CY"->166,
      "OB"->166,
       "RL"-> 163,
       "NIL" -> 160)

    val planMap = Map(
      "Monthly" -> 84
    )

    var flag = true

    val csvData = new CSVUtils("/tmp/Mohan_Data_1.csv", true)
    for (values <- csvData.all) {
      if (i != 0 && flag) {
        println(values)
        //case class Connection(id: Option[Int], customerId: Option[Int], setupBoxId: String, boxSerialNo:String, planId: Int, discount: Int, installationDate: DateTime,
        //                     status: String, cafId: String, idProof: String, companyId: Option[Int])

        //        case class CustomerCapsule(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String],
        //                                   address: String, companyId: Int, areaId: Int, houseNo: Option[String], balanceAmount: Int,
        //                                   createdBy: Option[Int], updatedBy: Option[Int], connections: List[Connection])
        val cust = CustomerCapsule(None,
          values(0),
          (if (values(1).replace(" ", "").length > 0) Some(values(1).replace(" ", "").toLong) else None),
          Some(values(2).trim),
          values(5),
          companyId,
          areaMap.getOrElse(values(4).trim, throw new Exception("Error Parsing")),
          None,
          Try(values(3).trim.toInt) match {
            case Success(x) => x
            case Failure(e) => 0
          },
          None,
          None,
          List(Connection(None,
            None,
            values(6).trim,
            values(8).trim,
            planMap.getOrElse(values(10).trim, 84),
            0,
            DateTime.now(),
            "ACTIVE",
            "NIL",
            "NIL",
            Some(companyId),
            Some(ConnectionStatus.UNKNOWN),
            Some(false))))

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
