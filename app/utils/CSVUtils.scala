package utils

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv._

import scala.util.control.Exception

class CSVUtils(filePath: String, ignoreHeader: Boolean) {
  private val reader = CSVReader.open(filePath)
  private val it = reader.iterator
  private var lineNo = 1

  def next: Option[List[String]] = {
    if (lineNo == 1 && ignoreHeader) {
      it.next()
      lineNo = 2
    }
    Exception.catching(classOf[Exception]).opt(it.next().toList)
  }

  def hasNext:Boolean = it.hasNext

  def all: List[List[String]] = {
    reader.all()
  }

  override def finalize(): Unit = {
    reader.close()
  }
}