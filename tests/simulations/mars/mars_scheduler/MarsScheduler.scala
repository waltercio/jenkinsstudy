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
 *  Based on: QX-4196
 *  Updated by: Alvaro Barbosa Moreira
 *  Based on: QX-4196/QX-13833
 */

class MarsScheduler extends BaseTest {

  val marsSchedulerResourceFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/mars/mars_scheduler_config.json").getLines().mkString)

  val deviceId = (marsSchedulerResourceFile \\ "deviceId" \\ environment).extract[String]

  // Name of each request
  val req1 = "GET - Verify inital device state"
  val req2 = "POST - Change the collectionStatus value to COMPLETED"
  val req3 = "GET - Verify if lastScheduleTime was updated"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MarsScheduler")

    //GET - Verify inital device state
    .exec(http(req1)
      .get("micro/mars_state/v2/" + deviceId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].deviceId").is(deviceId))
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].collectionStatus").exists)
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].lastScheduleTime").find.saveAs("foundScheduleTime"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //POST - Change the collectionStatus value to COMPLETED
    .exec(http(req2)
      .post("micro/mars_state/v2")
      .basicAuth(adUser, adPass)
      .body(StringBody("{ \"deviceId\": \"" + deviceId + "\", \"requestType\": \"qradar_offenses\", \"collectionStatus\": \"COMPLETED\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..deviceId").is(deviceId))
      .check(jsonPath("$..requestType").is("qradar_offenses"))
      .check(jsonPath("$..collectionStatus").is("COMPLETED"))
      .check(jsonPath("$..updatedAt").find.saveAs("foundUpdate"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies).pause (60 seconds)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

      //GET - Verify if lastScheduleTime was updated
     .exec(http(req3)
      .get("micro/mars_state/v2/" + deviceId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].deviceId").is(deviceId))
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].collectionStatus").exists)
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].collectionMessage").is("OK"))
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].lastScheduleTime").not("${foundScheduleTime}"))
      .check(jsonPath("$..[?(@.requestType == 'qradar_offenses')].updatedAt").not("${foundUpdate}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}