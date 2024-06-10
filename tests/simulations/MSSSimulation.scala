package src

import scala.concurrent.duration._
import scala.xml._
import scala.util.parsing.json._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import org.json4s.JsonAST.JValue
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.check.HttpCheck
import scala.collection.mutable.ListBuffer

class MSSSimulation extends Simulation {
  val environment = System.getenv("ENV")

  implicit val formats = DefaultFormats

  /**
   * Reads a json file and stores it into a Map.
   * jsonFilePath must be the full path to the json file, e.g.:
   * /home/mssuser/Services/resources/remedy_metadata/expected_schema_HDCustomerContacts.json"   *
   */
  def jsonFileToMap(jsonFilePath: String): Map[String, String] = {
    jsonStringToMap(Source.fromFile(jsonFilePath).getLines().mkString)
  }

  /**
   * Reads a json string and stores it into a Map.
   */
  def jsonStringToMap(jsonString: String): Map[String, String] = {
    val jsonFile: JValue = JsonMethods.parse(jsonString)
    jsonFile.values.asInstanceOf[Map[String, String]]
  }

  /**
   * From a Map containing json element name as key and element value as value, this method
   * returns a list of jsonPath HttpCheck objects.
   */
  def getJsonPathHttpChecks(expectedJsonMap: Map[String, Any]): List[HttpCheck] = {
    val jsonPathChecks: ListBuffer[HttpCheck] = ListBuffer()

    expectedJsonMap.keys.foreach(k => {
      jsonPathChecks.+=(jsonPath("$.." + k).is("" + expectedJsonMap.get(k).getOrElse("")))
    })

    jsonPathChecks.toList
  }
}
