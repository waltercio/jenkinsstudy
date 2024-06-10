import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Developed by: Renata Angelelli
    Date: 10/27/2020
 *  Based on: QX-3302
 */

class AiSystemStatisticMs extends BaseTest {

  // Name of each request
  val req1="GET request - ai_system_statistic_ms call w/ size/keys/statTimeStart/statTimeEnd parameters"

  //Getting current time to use as parameter
  val currentDate = java.time.LocalDate.now
  val date10DaysAgo = java.time.LocalDate.now.minusDays(10)

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiSystemStatisticMs")
 
    .exec(http(req1)
      .get("micro/ai_system_statistic/hourly")
      .queryParam("size", "5")
      .queryParam("keys", "logs.UniqueDeviceCount.today.total")
      .queryParam("keys", "logs.total")
      .queryParam("statTimeStart", date10DaysAgo + "T00:00:00.000Z")
      .queryParam("statTimeEnd", currentDate + "T09:00:00.000Z")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content[0]..key").exists)
      .check(jsonPath("$..content[0]..analysisType").exists)
      .check(jsonPath("$..content[0]..logType").exists)
      .check(jsonPath("$..content[0]..timeInterval").exists)
      .check(jsonPath("$..content[0]..statTime").exists)
      .check(jsonPath("$..content[0]..hostname").exists)
      .check(jsonPath("$..content[0]..module").exists)
      .check(jsonPath("$..content[0]..timeUnits").exists)
      .check(jsonPath("$..content[0]..count").exists)
      .check(jsonPath("$..content[0]..totalValue").exists)
      .check(jsonPath("$..content[0]..maxValue").exists)
      .check(jsonPath("$..content[0]..updateTime").exists)
      .check(jsonPath("$..content[1]..key").exists)
      .check(jsonPath("$..content[1]..analysisType").exists)
      .check(jsonPath("$..content[1]..logType").exists)
      .check(jsonPath("$..content[1]..timeInterval").exists)
      .check(jsonPath("$..content[1]..statTime").exists)
      .check(jsonPath("$..content[1]..hostname").exists)
      .check(jsonPath("$..content[1]..module").exists)
      .check(jsonPath("$..content[1]..timeUnits").exists)
      .check(jsonPath("$..content[1]..count").exists)
      .check(jsonPath("$..content[1]..totalValue").exists)
      .check(jsonPath("$..content[1]..maxValue").exists)
      .check(jsonPath("$..content[1]..updateTime").exists)
      .check(jsonPath("$..content[2]..key").exists)
      .check(jsonPath("$..content[2]..analysisType").exists)
      .check(jsonPath("$..content[2]..logType").exists)
      .check(jsonPath("$..content[2]..timeInterval").exists)
      .check(jsonPath("$..content[2]..statTime").exists)
      .check(jsonPath("$..content[2]..hostname").exists)
      .check(jsonPath("$..content[2]..module").exists)
      .check(jsonPath("$..content[2]..timeUnits").exists)
      .check(jsonPath("$..content[2]..count").exists)
      .check(jsonPath("$..content[2]..totalValue").exists)
      .check(jsonPath("$..content[2]..maxValue").exists)
      .check(jsonPath("$..content[2]..updateTime").exists)
      .check(jsonPath("$..content[3]..key").exists)
      .check(jsonPath("$..content[3]..analysisType").exists)
      .check(jsonPath("$..content[3]..logType").exists)
      .check(jsonPath("$..content[3]..timeInterval").exists)
      .check(jsonPath("$..content[3]..statTime").exists)
      .check(jsonPath("$..content[3]..hostname").exists)
      .check(jsonPath("$..content[3]..module").exists)
      .check(jsonPath("$..content[3]..timeUnits").exists)
      .check(jsonPath("$..content[3]..count").exists)
      .check(jsonPath("$..content[3]..totalValue").exists)
      .check(jsonPath("$..content[3]..maxValue").exists)
      .check(jsonPath("$..content[3]..updateTime").exists)
      .check(jsonPath("$..content[4]..key").exists)
      .check(jsonPath("$..content[4]..analysisType").exists)
      .check(jsonPath("$..content[4]..logType").exists)
      .check(jsonPath("$..content[4]..timeInterval").exists)
      .check(jsonPath("$..content[4]..statTime").exists)
      .check(jsonPath("$..content[4]..hostname").exists)
      .check(jsonPath("$..content[4]..module").exists)
      .check(jsonPath("$..content[4]..timeUnits").exists)
      .check(jsonPath("$..content[4]..count").exists)
      .check(jsonPath("$..content[4]..totalValue").exists)
      .check(jsonPath("$..content[4]..maxValue").exists)
      .check(jsonPath("$..content[4]..updateTime").exists)
      .check(jsonPath("$..pageable..sort..orders").exists)
      .check(jsonPath("$..pageable..page").is("0"))
      .check(jsonPath("$..pageable..size").is("5"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}