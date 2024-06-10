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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9072
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8782
 *  Updated by: ALvaro Barbosa Moreira
 *  Jira Ticket: https://jira.sec.ibm.com/browse/QX-9825
 */

class MarsAzureNativeRequestBuilderMs extends BaseTest {

  val endpointUrl = (configurations \\ "marsEndpointURL" \\ environment).extract[String]

  // Name of each request
  val req1 = "POST - Build a Azure native log request with the endpoint /auditlogs"
  val req2 = "Negative Scenario - No Auth"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MarsAzureNativeRequestBuilderMs")

    //POST - Build a Azure native log request with the endpoint /auditlogs
    .exec(http(req1)
      .post(endpointUrl)
      .basicAuth(authToken, authPass)
      .body(StringBody("{ \"deviceId\":\"PR0000000041350\", \"requestType\": \"azurenative_activitylogs\", \"attributes\":{\"lastAuditLogTimestamp\":\"2021-06-24T12:00:00Z\"}}"))
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is("PR0000000041350"))
      .check(jsonPath("$..requestType").is("azurenative_activitylogs"))
      .check(jsonPath("$..collectorTopicName").is("Mars_azurenative_activitylogs_request"))
      .check(jsonPath("$..[?(@['Subscription ID'])]").exists)
      .check(jsonPath("$..[?(@['Tenant ID'])]").exists)
      .check(jsonPath("$..[?(@['MARS Polling'])]").exists)
      .check(jsonPath("$..[?(@['API Version'])]").exists)
      .check(jsonPath("$..[?(@['Grant Type'])]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative Scenario - No Auth
    .exec(http(req2)
      .post(endpointUrl)
      .body(StringBody("{ \"deviceId\":\"PR0000000041350\", \"requestType\": \"azurenative_activitylogs\", \"attributes\":{\"lastAuditLogTimestamp\":\"2021-06-24T12:00:00Z\"}}"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
} 