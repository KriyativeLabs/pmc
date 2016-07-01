import java.io.{FileWriter, BufferedWriter, File}
import java.nio.charset.CodingErrorAction

import controllers.CustomerCreate
import models.{Connection, Customers}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.io.{Source, Codec}

object Testing {

  def main(args: Array[String]) = {

    //println(Customers.getAllWithFilters(7, None, None, Some("7829728448"), None,None,None,None).length)
    val formatter = DateTimeFormat.forPattern("dd-MMM-yy")

    println(formatter.parseDateTime("21-Jul-16"))

    val file = new File("/tmp/rejectedfiles")
    val bw = new BufferedWriter(new FileWriter(file))

    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    var i = 0
    val areaMap = Map(
      "AVS" -> 105,
      "ALV" -> 106,
      "BCA" -> 107,
      "BCF" -> 108,
      "BCM" -> 109,
      "BCN" -> 110,
      "BCS" -> 111,
      "BTC" -> 112,
      "CRJ" -> 113,
      "DPA" -> 114,
      "GAG" -> 115,
      "GAV" -> 116,
      "JPH" -> 117,
      "KAT" -> 118,
      "KMU" -> 119,
      "KUT" -> 120,
      "MAN" -> 121,
      "MUT" -> 122,
      "PAT" -> 123,
      "PUS" -> 124,
      "SCS" -> 125,
      "SIL" -> 126,
      "TMM" -> 127,
      "YMC" -> 128)

    val planMap = Map(
      "1024kBBHULweb" -> 51,
      "2048kBBHULweb" -> 52,
      "4096kBBHULweb" -> 53,
      "512kBBHULweb" -> 54,
      "FUP10Mbps_40GB" -> 55,
      "FUP10Mbps-ONAM Special" -> 56,
      "FUP1Mbps_10GB" -> 57,
      "FUP1Mbps_20GB" -> 58,
      "FUP1Mbps_SPECIAL" -> 59,
      "FUP20Mbps_100GB" -> 60,
      "FUP2Mbps_10GB" -> 61,
      "FUP2Mbps_20GB" -> 62,
      "FUP4Mbps_30GB" -> 63,
      "FUP4Mbps_40GB" -> 64,
      "FUP4Mbps-ONAM Special" -> 65,
      "FUP8Mbps_40GB" -> 66,
      "SME_1Mbps_UL" -> 67,
      "SME_2Mbps_UL" -> 68,
      "SME_4Mbps_UL-1" -> 69,
      "SME_4Mbps_UL-2" -> 70,
      "SME_4Mbps_UL" -> 70,
      "SME_8Mbps_UL" -> 71,
      "FUP10Mbps_60GB" -> 72)


    var flag = true
    for (line <- Source.fromFile("/Users/surya/Downloads/Excel_Header_Arun_Kerala.csv")(decoder).getLines()) {

      val values = line.split(",")

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
          "Thiruvalla, Pathanamthitta(Dist), Kerala",
          areaMap.getOrElse(values(4).trim, throw new Exception("Error Parsing")),
          0,
          List(Connection(None, None,
            values(6).trim,
            values(7),
            planMap.getOrElse(values(10).trim, throw new Exception("Error Parsing")),
            0,
            formatter.parseDateTime(values(13)),
            "ACTIVE",
            "NIL",
            "NIL",
            Some(14))))

        println(cust)
        //        println(values(5))

        Customers.tempInsert(cust, 14) match {
          case Right(r) => println(r)
          case Left(l) => println(l);bw.write(line); bw.newLine();
        }

      }
      i = i + 1
    }
    bw.close()
  }
}
