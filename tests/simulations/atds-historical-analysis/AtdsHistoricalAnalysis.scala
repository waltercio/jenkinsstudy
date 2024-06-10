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
import io.gatling.core.session.Session

/**
 * Developed by: vatamaniuc.eugeniu@ibm.com
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-11702
 * Functional test link: https://jira.sec.ibm.com/browse/QX-11705
 */

class AtdsHistoricalAnalysis extends BaseTest {

  //local variable
  val atdsHistoricalAnalysis = "micro/atds-historical-analysis/analyse"

  //payload paths
  val payload = "/tests/resources/atds_historical_analysis/atds_historical_analysis_ms_payload.json"
  val payloadNegativeTest = "/tests/resources/atds_historical_analysis/atds_historical_analysis_ms_payload_negative_test.json"

  //  Name of each request
  val req01 = "Post ATDS Historical Analisys data"
  val req02 = "POST call with negative scenario-Invalid Payload"
  val req03 = "Post Call with negative scenario - Invalid ID"
  val req04 = "Post Call with negative scenario - Invalid password"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AtdsHistoricalAnalysis")

    //Post ATDS Historical Analisys data
    .exec(http(req01)
      .post(atdsHistoricalAnalysis)
      .body(RawFileBody(currentDirectory + payload)).asJson
      .check(status.is(200))
      .check(jsonPath("$..details").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST call with negative scenario-Invalid Payload
    .exec(http(req02)
      .post(atdsHistoricalAnalysis)
      .body(RawFileBody(currentDirectory + payloadNegativeTest)).asJson
      .check(status.is(422))
      .check(jsonPath("$..type").is("value_error.missing"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Post Call with negative scenario - Invalid ID
    .exec(http(req03)
      .post(atdsHistoricalAnalysis)
      .body(RawFileBody(currentDirectory + payload)).asJson
      .basicAuth("test", adPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Post Call with negative scenario - Invalid password
    .exec(http(req04)
      .post(atdsHistoricalAnalysis)
      .body(RawFileBody(currentDirectory + payload)).asJson
      .basicAuth(adUser, "test")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}