import java.nio.charset.CodingErrorAction

import controllers.CustomerCreate
import models.{Customers, Connection}
import org.joda.time.DateTime

/**
 * Created by suryap on 27/10/15.
 */
import scala.io.Source
import scala.io.Codec
object Testing {

  def main(args:Array[String]) = {

    //println(DateTime.now().minusMonths(1).dayOfMonth().withMaximumValue().withTime(23,59,59,999))
    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    var i = 0
    for(line <- Source.fromFile("/Personal/NextUploads-1.csv")(decoder).getLines()){

      val values = line.split(",")
      if(i!=0) {

        //case class CustomerCreate(id: Option[Int], name: String, mobileNo: Option[Long], emailId: Option[String],
        // address: String, areaId: Int, balanceAmount: Int, connections: List[Connection])
        //case class Connection(id: Option[Int], customerId: Option[Int], setupBoxId: String, boxSerialNo:String, planId: Int, discount: Int, installationDate: DateTime,
        //                     status: String, cafId: String, idProof: String, companyId: Option[Int])
        val areaMap = Map(
          "BH" -> 1,
          "AJ" -> 2,
          "BA" -> 3,
          "BS" -> 4,
          "CG" -> 5,
          "CN" -> 6,
          "DP" -> 7,
          "GT" -> 8,
          "HO" -> 9,
          "KG" -> 10,
          "KK" -> 11,
          "KT" -> 12,
          "MAG" -> 13,
          "ML" -> 14,
          "MST" -> 15,
          "MC" -> 16,
          "NC" -> 17,
          "PT" -> 18,
          "RS" -> 19,
          "SK" -> 20,
          "SVP" -> 21,
          "TC" -> 22,
          "VH" -> 24,
          "VT" -> 25)
        val cust = CustomerCreate(None, values(1), (if (values(2).length > 0) Some(values(2).toLong) else None), None, values(5), areaMap.getOrElse(values(4), 1), (if(values(3).length >0) values(3).toInt else 0),
          List(Connection(None, None, values(6), values(8), 1, 0, DateTime.now(), "ACTIVE", values(7), values(12), Some(1))))
        Customers.tempinsert(cust)
      }
      i = i+1
    }
  }
}
