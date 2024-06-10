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
 *  Developed by: guibasa@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10023
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10025
 *  PLEASE, MAKE SURE TO EXECUTE THE TEST ON ALL ENVIRONMENTS.
 */

 class AiSiemSyncActionValidatorMs extends BaseTest {

  //local variables
  val endPoint = "micro/ai-siem-sync-action-validator/qradar-offense-close-decision"

  // Name of each request
  val req1 = "Check if the offense is available to close"
  val req2 = "Check no auth"
  val req3 = "No DeviceId"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiSiemSyncActionValidatorMs")

    //Check if the offense is available to close
    .exec(http(req1)
      .get(endPoint)
      .queryParam("deviceIds", "PR0000000037877")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..qradarOffenseCloseDecisions").exists)
      .check(jsonPath("$..deviceId").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..shouldCloseOffense").exists)
      .check(jsonPath("$..explanationForCloseDecision").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Check no auth
    .exec(http(req2)
      .get(endPoint)
      .queryParam("deviceIds", "PR0000000037877")
      .basicAuth("", "")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //No DeviceId
    .exec(http(req3)
      .get(endPoint)
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..error").is("Bad Request"))
      .check(jsonPath("$..message").is("Required request parameter 'deviceIds' for method parameter type String is not present"))
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