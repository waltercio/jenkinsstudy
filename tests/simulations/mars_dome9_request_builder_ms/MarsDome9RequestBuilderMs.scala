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
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 *  Developed by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9071
 *  Functional test link: https://jira.sec.ibm.com/browse/XPS-70034
 *  Updated by: Alvaro Barbosa Moreira
 *  Automation maintenance task for this script:
 */

class MarsDome9RequestBuilderMs extends BaseTest {

  val marsDome9RequestBuilderResourceFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/mars_dome9_request_builder_ms/configuration.json").getLines().mkString)
//  val endpointUrl = (marsQradarMetricRequestBuilderResourceFile \\ "endpointURL" \\ environment).extract[String]
  val endpointUrl = (marsDome9RequestBuilderResourceFile \\ "endpointURL" \\ environment).extract[String]

  // Name of each request
  val req1 = "GET Healthcheck"
  val req2 = "POST - Build a Dome9 log request with the endpoint /auditlogs"
  val req3 = "Negative Scenario - No Auth"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3  = "jsession3"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MarsDome9RequestBuilderMs")

    .exec(http(req1)
      .get(endpointUrl)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..numberFive").is("I'm Alive!"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1)) //This service has no cookie
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post(baseUrl + "micro/mars-dome9-request-builder-ms/auditlogs")
      .body(StringBody("{ \"deviceId\":\"PR0000000041639\", \"requestType\": \"dome9_auditlogs\", \"attributes\":{\"lastAuditLogTimestamp\":\"2021-06-24T12:00:00Z\"}}"))
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is("PR0000000041639"))
      .check(jsonPath("$..requestType").is("dome9_auditlogs"))
      .check(jsonPath("$..collectorTopicName").is("MARS_dome9_auditlogs_request"))
      .check(jsonPath("$..[?(@['Account ID'])]").exists)
      .check(jsonPath("$..Tenant").exists)
      .check(checkIf(environment == "DEV"){jsonPath("$..[?(@['API Url'])]").exists})
      .check(checkIf(environment != "DEV"){jsonPath("$..[?(@['API Url'])]").exists})
      .check(jsonPath("$..[?(@['MARS Polling'])]").exists)
      .check(jsonPath("$..[?(@['Cluster Hostname'])]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .post(baseUrl + "micro/mars-dome9-request-builder-ms/auditlogs")
      .body(StringBody("{ \"deviceId\":\"PR0000000041639\", \"requestType\": \"dome9_auditlogs\", \"attributes\":{\"lastAuditLogTimestamp\":\"2021-06-24T12:00:00Z\"}}"))
      .basicAuth("Anything", adPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
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